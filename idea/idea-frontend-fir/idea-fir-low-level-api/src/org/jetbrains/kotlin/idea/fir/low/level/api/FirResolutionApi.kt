/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.fir.low.level.api

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.fir.*
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.*
import org.jetbrains.kotlin.fir.resolve.FirTowerDataContext
import org.jetbrains.kotlin.fir.resolve.ResolutionMode
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.providers.FirProvider
import org.jetbrains.kotlin.fir.resolve.providers.getClassDeclaredCallableSymbols
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirBodyResolveTransformer
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.createReturnTypeCalculatorForIDE
import org.jetbrains.kotlin.fir.resolve.transformers.runResolve
import org.jetbrains.kotlin.fir.scopes.impl.FirPackageMemberScope
import org.jetbrains.kotlin.fir.symbols.CallableId
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.types.FirErrorTypeRef
import org.jetbrains.kotlin.fir.types.FirUserTypeRef
import org.jetbrains.kotlin.fir.visitors.CompositeTransformResult
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.fir.visitors.compose
import org.jetbrains.kotlin.idea.util.getElementTextInContext
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

private val FirResolvePhase.stubMode: Boolean
    get() = this <= FirResolvePhase.DECLARATIONS

private fun KtClassOrObject.relativeFqName(): FqName {
    val className = this.nameAsSafeName
    val parentFqName = this.containingClassOrObject?.relativeFqName()
    return parentFqName?.child(className) ?: FqName.topLevel(className)
}

private fun FirFile.findCallableMember(
    provider: FirProvider, callableMember: KtCallableDeclaration,
    packageFqName: FqName, klassFqName: FqName?, declName: Name
): FirCallableDeclaration<*> {
    if (klassFqName != null) {
        return provider.getClassDeclaredCallableSymbols(ClassId(packageFqName, klassFqName, false), declName)
            .find { symbol: FirCallableSymbol<*> ->
                symbol.fir.psi == callableMember
            }?.fir
            ?: error("Cannot find FIR callable declaration ${CallableId(packageFqName, klassFqName, declName)}")
    }
    // NB: not sure it's correct to use member scope provider from here (because of possible changes)
    val memberScope = FirPackageMemberScope(this.packageFqName, session)
    var result: FirCallableDeclaration<*>? = null
    val processor = { symbol: FirCallableSymbol<*> ->
        val fir = symbol.fir
        if (result == null && fir.psi == callableMember) {
            result = fir
        }
    }
    if (callableMember is KtNamedFunction || callableMember is KtConstructor<*>) {
        memberScope.processFunctionsByName(declName, processor)
    } else {
        memberScope.processPropertiesByName(declName, processor)
    }

    return result
        ?: error("Cannot find FIR callable declaration ${CallableId(packageFqName, klassFqName, declName)}")
}

internal fun KtCallableDeclaration.getOrBuildFir(
    state: FirModuleResolveState,
    phase: FirResolvePhase = FirResolvePhase.DECLARATIONS
): FirCallableDeclaration<*> {
    val session = state.getSession(this)

    val file = this.containingKtFile
    val packageFqName = file.packageFqName
    val klassFqName = this.containingClassOrObject?.relativeFqName()
    val declName = this.nameAsSafeName

    val firProvider = session.firIdeProvider
    val firFile = firProvider.getOrBuildFile(file)
    val firMemberSymbol = firFile.findCallableMember(firProvider, this, packageFqName, klassFqName, declName).symbol
    val firMemberDeclaration = firMemberSymbol.fir
    if (firMemberDeclaration.resolvePhase >= phase) {
        return firMemberDeclaration
    }
    synchronized(firFile) {
        firMemberDeclaration.runResolve(firFile, firProvider, phase, state)
    }
    return firMemberDeclaration
}

internal fun KtClassOrObject.getOrBuildFir(
    state: FirModuleResolveState,
    phase: FirResolvePhase = FirResolvePhase.DECLARATIONS
): FirMemberDeclaration {
    val session = state.getSession(this)

    val file = this.containingKtFile
    val packageFqName = file.packageFqName
    val klassFqName = this.relativeFqName()

    val firProvider = session.firIdeProvider
    val firFile = firProvider.getOrBuildFile(file)

    val firClassOrEnumEntry = if (this is KtEnumEntry) {
        val firEnumClass = firProvider.getFirClassifierByFqName(ClassId(packageFqName, klassFqName.parent(), false)) as FirRegularClass
        firEnumClass.declarations.first { it is FirEnumEntry && it.name == this.nameAsSafeName } as FirMemberDeclaration
    } else {
        firProvider.getFirClassifierByFqName(ClassId(packageFqName, klassFqName, false)) as FirRegularClass
    }
    if (firClassOrEnumEntry.resolvePhase >= phase) {
        return firClassOrEnumEntry
    }
    synchronized(firFile) {
        firClassOrEnumEntry.runResolve(firFile, firProvider, phase, state)
    }
    return firClassOrEnumEntry
}

private fun KtFile.getOrBuildRawFirFile(state: FirModuleResolveState): Pair<FirIdeProvider, FirFile> {
    val session = state.getSession(this)
    val firProvider = session.firIdeProvider
    return firProvider to firProvider.getOrBuildFile(this)
}

internal fun KtFile.getOrBuildFir(
    state: FirModuleResolveState,
    phase: FirResolvePhase = FirResolvePhase.DECLARATIONS
): FirFile {
    val (firProvider, firFile) = getOrBuildRawFirFile(state)
    if (phase <= FirResolvePhase.DECLARATIONS && firFile.resolvePhase >= phase) {
        return firFile
    }
    synchronized(firFile) {
        firFile.runResolve(firFile, firProvider, phase, state)
    }
    return firFile
}

internal fun KtFile.getOrBuildFirWithDiagnostics(state: FirModuleResolveState): FirFile {
    val (_, firFile) = getOrBuildRawFirFile(state)
    val currentResolvePhase = firFile.resolvePhase
    if (currentResolvePhase < FirResolvePhase.BODY_RESOLVE) {
        synchronized(firFile) {
            firFile.runResolve(toPhase = FirResolvePhase.BODY_RESOLVE, fromPhase = currentResolvePhase)
        }
    }

    ProgressIndicatorProvider.checkCanceled() // ???
    if (state.hasDiagnosticsForFile(this)) return firFile

    FirIdeDiagnosticsCollector(firFile.session, state).collectDiagnostics(firFile)
    state.setDiagnosticsForFile(this, firFile)
    return firFile
}

internal fun FirDeclaration.runResolve(
    file: FirFile,
    firProvider: FirIdeProvider,
    toPhase: FirResolvePhase,
    state: FirModuleResolveState,
    towerDataContextForStatement: MutableMap<FirStatement, FirTowerDataContext>? = null,
) {
    val nonLazyPhase = minOf(toPhase, FirResolvePhase.DECLARATIONS)
    file.runResolve(toPhase = nonLazyPhase, fromPhase = this.resolvePhase)
    if (toPhase <= nonLazyPhase) return
    val designation = mutableListOf<FirDeclaration>(file)
    if (this !is FirFile) {
        val id = when (this) {
            is FirCallableDeclaration<*> -> {
                this.symbol.callableId.classId
            }
            is FirRegularClass -> {
                this.symbol.classId
            }
            else -> error("Unsupported: ${render()}")
        }
        val outerClasses = generateSequence(id) { classId ->
            classId.outerClassId
        }.mapTo(mutableListOf()) { firProvider.getFirClassifierByFqName(it)!! }
        designation += outerClasses.asReversed()
        if (this is FirCallableDeclaration<*>) {
            designation += this
        }
    }
    if (designation.all { it.resolvePhase >= toPhase }) {
        return
    }
    val scopeSession = ScopeSession()
    val transformer = FirDesignatedBodyResolveTransformerForIDE(
        designation.iterator(), state.getSession(psi as KtElement),
        scopeSession,
        implicitTypeOnly = toPhase == FirResolvePhase.IMPLICIT_TYPES_BODY_RESOLVE,
        towerDataContextForStatement
    )
    file.transform<FirFile, ResolutionMode>(transformer, ResolutionMode.ContextDependent)
}

private class FirDesignatedBodyResolveTransformerForIDE(
    private val designation: Iterator<FirElement>,
    session: FirSession,
    scopeSession: ScopeSession,
    implicitTypeOnly: Boolean,
    private val towerDataContextForStatement: MutableMap<FirStatement, FirTowerDataContext>? = null
) : FirBodyResolveTransformer(
    session,
    phase = FirResolvePhase.IMPLICIT_TYPES_BODY_RESOLVE,
    implicitTypeOnly = implicitTypeOnly,
    scopeSession = scopeSession,
    returnTypeCalculator = createReturnTypeCalculatorForIDE(session, scopeSession)
) {

    override fun transformDeclarationContent(declaration: FirDeclaration, data: ResolutionMode): CompositeTransformResult<FirDeclaration> {
        if (designation.hasNext()) {
            designation.next().visitNoTransform(this, data)
            return declaration.compose()
        }

        return super.transformDeclarationContent(declaration, data)
    }

    override fun onBeforeStatementResolution(statement: FirStatement) {
        if (towerDataContextForStatement == null) return
        towerDataContextForStatement[statement] = context.towerDataContext
    }
}


private fun KtElement.getNonLocalContainingDeclarationWithFqName(): KtDeclaration? {
    var container = parent
    while (container != null && container !is KtFile) {
        if (container is KtDeclaration
            && (container is KtClassOrObject || container is KtDeclarationWithBody)
            && !KtPsiUtil.isLocal(container)
            && container.name != null
            && container !is KtEnumEntry
            && container.containingClassOrObject !is KtEnumEntry
        ) {
            return container
        }
        container = container.parent
    }
    return null
}

internal fun KtElement.getOrBuildFir(
    state: FirModuleResolveState,
    phase: FirResolvePhase = FirResolvePhase.BODY_RESOLVE
): FirElement {
    val containerFir: FirDeclaration =
        when (val container = getNonLocalContainingDeclarationWithFqName()) {
            is KtCallableDeclaration -> container.getOrBuildFir(state, phase)
            is KtClassOrObject -> container.getOrBuildFir(state, phase)
            null -> containingKtFile.getOrBuildFir(state, phase)
            else -> error("Unsupported: ${container.text}")
        }

    val psi = when {
        this is KtPropertyDelegate -> this.expression ?: this
        this is KtQualifiedExpression && selectorExpression is KtCallExpression -> {
            /*
             KtQualifiedExpression with KtCallExpression in selector transformed in FIR to FirFunctionCall expression
             Which will have a receiver as qualifier
             */
            selectorExpression ?: error("Incomplete code:\n${this.getElementTextInContext()}")
        }
        else -> this
    }
    return state.getCachedMapping(this) ?: run {
        state.recordElementsFrom(containerFir)

        val (current, mappedFir) = psi.getFirOfClosestParent(state) ?: error("FirElement is not found for: $text")
        if (current != this) {
            state.record(current, mappedFir)
        }

        mappedFir
    }
}

internal fun KtElement.getFirOfClosestParent(state: FirModuleResolveState): Pair<KtElement, FirElement>? {
    var current: PsiElement? = this
    while (current is KtElement) {
        val mappedFir = state.getCachedMapping(current)
        if (mappedFir != null) {
            return current to mappedFir
        }
        current = current.parent
    }

    return null
}

internal fun FirModuleResolveState.recordElementsFrom(containerFir: FirDeclaration) {
    containerFir.accept(object : FirVisitorVoid() {
        override fun visitElement(element: FirElement) {
            (element.realPsi as? KtElement)?.let {
                record(it, element)
            }
            element.acceptChildren(this)
        }

        override fun visitReference(reference: FirReference) {}

        override fun visitControlFlowGraphReference(controlFlowGraphReference: FirControlFlowGraphReference) {}

        override fun visitNamedReference(namedReference: FirNamedReference) {}

        override fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference) {}

        override fun visitDelegateFieldReference(delegateFieldReference: FirDelegateFieldReference) {}

        override fun visitBackingFieldReference(backingFieldReference: FirBackingFieldReference) {}

        override fun visitSuperReference(superReference: FirSuperReference) {}

        override fun visitThisReference(thisReference: FirThisReference) {}

        override fun visitErrorTypeRef(errorTypeRef: FirErrorTypeRef) {}

        override fun visitUserTypeRef(userTypeRef: FirUserTypeRef) {
            userTypeRef.acceptChildren(this)
        }
    })
}
