package geometry
class GeneralNode extends BaseNode
{
    GeneralNode(Shape shape, String name) {
         super("generalnode")
    }
    GeneralNode(BaseNode o) {
        super(o)
    }

    GeneralNode copy() {
        return new GeneralNode(this)
    }
}
