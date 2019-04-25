<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output omit-xml-declaration="yes" method="html"/>
<xsl:template match="text()">
<xsl:value-of select="."/>
</xsl:template>
<xsl:template match="@*"></xsl:template>
<xsl:template match="br">
<xsl:element name="br"/>
</xsl:template>
<xsl:template match="p">
<xsl:element name="p">
<xsl:apply-templates/>
</xsl:element>
</xsl:template>
<xsl:template match="*">
<xsl:apply-templates/>
</xsl:template>
</xsl:stylesheet>