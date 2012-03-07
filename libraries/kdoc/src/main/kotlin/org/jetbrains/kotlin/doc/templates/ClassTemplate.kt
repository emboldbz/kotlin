package org.jetbrains.kotlin.doc.templates

import kotlin.*
import org.jetbrains.kotlin.template.*
import kotlin.io.*
import kotlin.util.*
import java.util.*
import org.jetbrains.kotlin.doc.model.KModel
import org.jetbrains.kotlin.doc.model.KPackage
import org.jetbrains.kotlin.doc.model.KClass
import org.jetbrains.kotlin.doc.model.KFunction
import org.jetbrains.kotlin.doc.model.KAnnotation

open class ClassTemplate(open val model: KModel, pkg: KPackage, open val klass: KClass) : PackageTemplateSupport(pkg) {

    open fun pageTitle(): String = "${klass.name} (${model.title})"

    override fun render() {
        println("""<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--NewPage-->
<HTML>
<HEAD>
<!-- Generated by kdoc (${model.version}) on ${Date()} -->
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<TITLE>
${pageTitle()}
</TITLE>

<META NAME="date" CONTENT="2012-01-09">
<META NAME="date" CONTENT="2012-01-09">
<LINK REL="stylesheet" TYPE="text/css" HREF="${pkg.nameAsRelativePath}stylesheet.css" TITLE="Style">

<SCRIPT type="text/javascript">
function windowTitle()
{
    if (location.href.indexOf('is-external=true') == -1) {
        parent.document.title="${klass.name} (${model.title})";
    }
}
</SCRIPT>
<NOSCRIPT>
</NOSCRIPT>

</HEAD>

<BODY BGCOLOR="white" onload="windowTitle();">
<HR>


<!-- ========= START OF TOP NAVBAR ======= -->
<A NAME="navbar_top"><!-- --></A>
<A HREF="#skip-navbar_top" title="Skip navigation links"></A>
<TABLE BORDER="0" WIDTH="100%" CELLPADDING="1" CELLSPACING="0" SUMMARY="">
<TR>
<TD COLSPAN=2 BGCOLOR="#EEEEFF" CLASS="NavBarCell1">
<A NAME="navbar_top_firstrow"><!-- --></A>
<TABLE BORDER="0" CELLPADDING="0" CELLSPACING="3" SUMMARY="">
  <TR ALIGN="center" VALIGN="top">
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}overview-summary.html"><FONT CLASS="NavBarFont1"><B>Overview</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-summary.html"><FONT CLASS="NavBarFont1"><B>Package</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1Rev"> &nbsp;<FONT CLASS="NavBarFont1Rev"><B>Class</B></FONT>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="class-use/${klass.simpleName}.html"><FONT CLASS="NavBarFont1"><B>Use</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-tree.html"><FONT CLASS="NavBarFont1"><B>Tree</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}deprecated-list.html"><FONT CLASS="NavBarFont1"><B>Deprecated</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}index-all.html"><FONT CLASS="NavBarFont1"><B>Index</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}help-doc.html"><FONT CLASS="NavBarFont1"><B>Help</B></FONT></A>&nbsp;</TD>
  </TR>
</TABLE>
</TD>
<TD ALIGN="right" VALIGN="top" ROWSPAN=3><EM>
</EM>
</TD>
</TR>

<TR>
""")

        printPrevNextClass()
        println("""<TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
  <A HREF="${pkg.nameAsRelativePath}index.html?${klass.nameAsPath}.html" target="_top"><B>FRAMES</B></A>  &nbsp;
&nbsp;<A HREF="${klass.simpleName}.html" target="_top"><B>NO FRAMES</B></A>  &nbsp;
&nbsp;<SCRIPT type="text/javascript">
  <!--
  if(window==top) {
    document.writeln('<A HREF="${pkg.nameAsRelativePath}allclasses-noframe.html"><B>All Classes</B></A>');
  }
  //-->
</SCRIPT>
<NOSCRIPT>
  <A HREF="${pkg.nameAsRelativePath}allclasses-noframe.html"><B>All Classes</B></A>
</NOSCRIPT>


</FONT></TD>
</TR>
<TR>
<TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
  SUMMARY:&nbsp;<A HREF="#nested_class_summary">NESTED</A>&nbsp;|&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_summary">METHOD</A></FONT></TD>
<TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
DETAIL:&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_detail">METHOD</A></FONT></TD>
</TR>
</TABLE>
<A NAME="skip-navbar_top"></A>
<!-- ========= END OF TOP NAVBAR ========= -->
""")

        printBody()

        println("""<!-- ======= START OF BOTTOM NAVBAR ====== -->
        <A NAME="navbar_bottom"><!-- --></A>
        <A HREF="#skip-navbar_bottom" title="Skip navigation links"></A>
        <TABLE BORDER="0" WIDTH="100%" CELLPADDING="1" CELLSPACING="0" SUMMARY="">
        <TR>
        <TD COLSPAN=2 BGCOLOR="#EEEEFF" CLASS="NavBarCell1">
        <A NAME="navbar_bottom_firstrow"><!-- --></A>
        <TABLE BORDER="0" CELLPADDING="0" CELLSPACING="3" SUMMARY="">
          <TR ALIGN="center" VALIGN="top">
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}overview-summary.html"><FONT CLASS="NavBarFont1"><B>Overview</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-summary.html"><FONT CLASS="NavBarFont1"><B>Package</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#FFFFFF" CLASS="NavBarCell1Rev"> &nbsp;<FONT CLASS="NavBarFont1Rev"><B>Class</B></FONT>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="class-use/${klass.simpleName}.html"><FONT CLASS="NavBarFont1"><B>Use</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-tree.html"><FONT CLASS="NavBarFont1"><B>Tree</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}deprecated-list.html"><FONT CLASS="NavBarFont1"><B>Deprecated</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}index-all.html"><FONT CLASS="NavBarFont1"><B>Index</B></FONT></A>&nbsp;</TD>
  <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="${pkg.nameAsRelativePath}help-doc.html"><FONT CLASS="NavBarFont1"><B>Help</B></FONT></A>&nbsp;</TD>
  </TR>
        </TABLE>
        </TD>
        <TD ALIGN="right" VALIGN="top" ROWSPAN=3><EM>
        </EM>
        </TD>
        </TR>

        <TR>""")
                printPrevNextClass()
                println("""<TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
  <A HREF="${pkg.nameAsRelativePath}index.html?${klass.nameAsPath}.html" target="_top"><B>FRAMES</B></A>  &nbsp;
&nbsp;<A HREF="${klass.simpleName}.html" target="_top"><B>NO FRAMES</B></A>  &nbsp;
&nbsp;<SCRIPT type="text/javascript">
          <!--
          if(window==top) {
    document.writeln('<A HREF="${pkg.nameAsRelativePath}allclasses-noframe.html"><B>All Classes</B></A>');
  }
          //-->
        </SCRIPT>
        <NOSCRIPT>${pkg.nameAsRelativePath}allclasses-noframe.html"><B>All Classes</B></A>
</NOSCRIPT>


        </FONT></TD>
        </TR>
        <TR>
        <TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
          SUMMARY:&nbsp;<A HREF="#nested_class_summary">NESTED</A>&nbsp;|&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_summary">METHOD</A></FONT></TD>
        <TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
        DETAIL:&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_detail">METHOD</A></FONT></TD>
        </TR>
        </TABLE>
        <A NAME="skip-navbar_bottom"></A>
        <!-- ======== END OF BOTTOM NAVBAR ======= -->

        <HR>
        Copyright &#169; 2010-2012. All Rights Reserved.
        </BODY>
        </HTML>""")
    }

    open fun printBody(): Unit {
        println("""<HR>
<!-- ======== START OF CLASS DATA ======== -->
<H2>
<FONT SIZE="-1">
${pkg.name}</FONT>
<BR>
Class ${klass.simpleName}</H2>
<PRE>""")

        for (bc in klass.baseClasses) {
            println(link(bc, true))
        }
        println("""  <IMG SRC="${pkg.nameAsRelativePath}resources/inherit.gif" ALT="extended by "><B>${klass.name}</B>
</PRE>
<HR>
<DL>
<DT><PRE><FONT SIZE="-1">""")
        printAnnotations(klass.annotations)
        print("""</FONT>public class <A HREF="${sourceHref(klass)}"><B>${klass.simpleName}</B></A><DT>""")
        if (!klass.baseClasses.isEmpty()) {
            print("""extends """)
            for (bc in klass.baseClasses) {
                println(link(bc))
            }
        }
        println("""</DL>
</PRE>

<P>""")
        println(klass.detailedDescription(this))
        if (klass.since.size > 0 || klass.authors.size > 0) {
            println("""<P>
<DL>""")
            if (klass.since.size > 0) {
                println("""<DT><B>Since:</B></DT>
  <DD>${klass.since}</DD>""")
            }
            for (author in klass.authors) {
                println("""<DT><B>Author:</B></DT>
  <DD>${author}</DD>""")
            }
            println("""</DL>""")
        }

        println("""<HR>

<P>""")
        val nestedClasses = klass.nestedClasses
        if (!nestedClasses.isEmpty()) {
            println("""<!-- ======== NESTED CLASS SUMMARY ======== -->

<A NAME="nested_class_summary"><!-- --></A>
<TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
<TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
<TH ALIGN="left" COLSPAN="2"><FONT SIZE="+2">
<B>Nested Class Summary</B></FONT></TH>
</TR>""")
            for (nc in nestedClasses) {
                println("""<TR BGCOLOR="white" CLASS="TableRowColor">
<TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
<CODE>static&nbsp;class</CODE></FONT></TD>
<TD><CODE><B><A HREF="${pkg.nameAsRelativePath}${nc.nameAsPath}.html" title="class in ${nc.packageName}">${klass.simpleName}.${nc.simpleName}</A></B></CODE>
<BR>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${nc.description(this)}
</TD>""")
            }
            println("""</TR>
</TABLE>
&nbsp;""")
        }

        printPropertySummary(klass.properties)
        printFunctionSummary(klass.functions)
        printFunctionDetail(klass.functions)

        println("""<!-- ========= END OF CLASS DATA ========= -->
<HR>
""")
    }

    open fun printPrevNextClass(): Unit {
        println("""<TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">""")
        val prev = pkg.previous(klass)
        if (prev != null) {
            println("""&nbsp;<A HREF="${pkg.nameAsRelativePath}${prev.nameAsPath}.html" title="class in ${prev.packageName}"><B>PREV CLASS</B></A>&nbsp;""")

        }
        val next = pkg.next(klass)
        if (next != null) {
            println("""&nbsp;<A HREF="${pkg.nameAsRelativePath}${next.nameAsPath}.html" title="class in ${next.packageName}"><B>NEXT CLASS</B></A>""")
        }
        println("""</FONT></TD>""")
    }

}
