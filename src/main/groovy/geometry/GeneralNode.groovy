package geometry
class GeneralNode extends BaseNode
{
    GeneralNode(Shape shape, String name) {
         super(shape,name)
    }
    GeneralNode(GeneralNode o) {
        super(o)
    }

    GeneralNode copy() {
        return new GeneralNode(this)
    }
}
