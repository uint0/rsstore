package dev.uint0.rsstore.helpers

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

class XMLUtil {
    private val root: Node

    val element
        get() = this.root as? Element ?: throw TypeCastException("Cannot use non-element node as element")
    val text get() = this.root.textContent.trim()

    constructor(xmlString: String) {
        val factory = DocumentBuilderFactory.newInstance()
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(xmlString)))
        document.documentElement.normalize()
        this.root = document.documentElement
    }

    constructor(root: Node) {
        this.root = root
    }

    fun getChildren(tagName: String): List<XMLUtil> {
        val elements = this.element.getElementsByTagName(tagName)
        return when (elements.length) {
            0 -> emptyList()
            else -> (0 until elements.length).map { XMLUtil(elements.item(it)) }
        }
    }

    fun getChild(tagName: String): XMLUtil = this.getChildren(tagName).firstOrNull()
        ?: throw NoSuchElementException("Element has no children of tag $tagName")
}
