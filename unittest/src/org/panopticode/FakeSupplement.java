package org.panopticode;

public class FakeSupplement implements Supplement {
    public int optionLength() {
        return 1;
    }

    public void loadData(PanopticodeProject project, String[] arguments) {
    }

    public SupplementDeclaration getDeclaration() {
        return new SupplementDeclaration("org.panopticode.FakeSupplement");
    }
}
