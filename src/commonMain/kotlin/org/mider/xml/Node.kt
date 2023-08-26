package org.mider.xml

class NodeContent {
    var contentIsNodes: Boolean = false
    var contentString: String = ""
    var nodes: MutableList<Node> = mutableListOf()

    constructor(str: String = "") {
        contentString = str
        if (contentString == "") contentIsNodes = true
    }

    constructor(lists: MutableList<Node>) {
        nodes = lists
        contentIsNodes = true
    }

    constructor(node: Node) : this(mutableListOf(node))

    operator fun plusAssign(node: Node) {
        nodes += node
    }
}

open class DeepNode(name: String, vararg attributes: Pair<String, Any>) : Node(name, *attributes) {
    init {
        contentIsNode = true
    }
}

open class Node (
    val name: String,
    val attributes: MutableMap<String, Any> = mutableMapOf(),
    val children: NodeContent = NodeContent(),
    var parent: Node? = null
) {

    constructor(name: String, attributes: MutableMap<String, Any>, content: String)
        : this (name, attributes, NodeContent(content))
    constructor(name: String, attributes: MutableMap<String, Any>, content: MutableList<Node>)
        : this (name, attributes, NodeContent(content))
    constructor(name: String, attributes: MutableMap<String, Any>, content: Node)
        : this (name, attributes, NodeContent(content))
    constructor(name: String, attributes: MutableMap<String, Any>, vararg content: Node)
        : this (name, attributes, content.toMutableList())
    constructor(name: String, vararg content: Node)
        : this (name, mutableMapOf(), content.toMutableList())
    constructor(name: String, vararg attributes: Pair<String, Any>)
        : this (name, attributes.toMap().toMutableMap(), "")
    constructor(name: String, content: String)
        : this (name, mutableMapOf(), content)
    constructor(name: String, content: Number)
        : this (name, mutableMapOf(), content.toString())
    constructor(name: String, content: Boolean)
        : this (name, mutableMapOf(), content.toString())
    constructor(name: String)
        : this (name, mutableMapOf(), "")

    var contentIsNode = children.contentIsNodes

    operator fun plusAssign(node: Node) {
        children += node
        node.parent = this
    }

    operator fun get(tagName: String) = children.nodes.filter { name == tagName }

    operator fun get(index: Int) = children.nodes[index]

    operator fun set(index: Int, value: Node) {
        children.nodes[index] = value
    }

    override fun toString(): String =
        "<$name${ 
            if (attributes.isNotEmpty()) 
                " " + attributes.map { 
                    it.key + "=" + "\"" + it.value + "\"" /*when(it.value) {
                        is CharSequence, Char -> 
                        else -> it.value
                    }*/
                }.joinToString(" ") 
            else ""
        }>" +
                (if (contentIsNode) children.nodes.joinToString("") else children.contentString) +
        "</$name>"
}