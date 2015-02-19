<#assign licenseFirst = "/*">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/${project.license}.txt">

<#if package?? && package != "">
package ${package};

</#if>
/**
 *
 * ${name}
 *
 * @author ${user}
 */
public class ${name} {

}
