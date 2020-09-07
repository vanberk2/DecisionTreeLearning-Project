import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class DTL {
    ArrayList<String[]> examples = new ArrayList<>();
    ArrayList<Integer> exampleIDs = new ArrayList<>();

    ArrayList<String> attributes = new ArrayList<>();
    ArrayList<Integer> attributesIDs = new ArrayList<>();

    ArrayList<String> targets = new ArrayList<>();
    ArrayList<Integer> targetCts = new ArrayList<>();

    ArrayList<Double> entropy = new ArrayList<>();
    Node defaultN;

    public DTL(ArrayList<String[]> examples, ArrayList<String> attributes, ArrayList<String> targets) {
        this.examples = examples;
        this.attributes = attributes;
        this.targets = targets;
        Integer yesCount = 0;
        Integer noCount = 0;
        targetCts.add(0);
        targetCts.add(0);

        // get counts of targets
        for (String t : targets) {
            if (t.equals("Yes")) {
                yesCount++;
                targetCts.set(0, yesCount);
            } else {
                noCount++;
                targetCts.set(1, noCount);
            }
        }

        // initialize example ids to examples
        for (int i = 0; i < examples.size(); i++) {
            exampleIDs.add(i);
        }

        // initialize attribute ids to attritbutes
        for (int i = 0; i < attributes.size(); i++) {
            attributesIDs.add(i);
        }

        // get initial entropy of examples
        for (int i = 0; i < targets.size(); i++) {
            entropy.add(getEntropy(exampleIDs));
        }

    }

    public void DTL() {
        defaultN = new Node();
        defaultN = DTL_(attributesIDs, exampleIDs, defaultN);
    }

    public Node DTL_(ArrayList<Integer> attributesIDs, ArrayList<Integer> exampleIDs, Node root) {
        root = new Node();

        if (exampleIDs.size() == 0) {
            return root;
        } else if (sameAttrTarget(exampleIDs)) {
            root.setValue(targets.get(exampleIDs.get(0)));
            return root;
        } else if (attributesIDs.size() == 0) {
            root.setValue(mode(exampleIDs));
            return root;
        }

        // find best attribute to split on
        Integer bestID = chooseAttribute(attributesIDs, exampleIDs);

        // find value of best attribute and set roots value to it
        root.setValue(attributes.get(bestID));

        ArrayList<String> newAttributes = new ArrayList<>();
        ArrayList<Integer> newAttributesCounts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> newAttributesIDs = new ArrayList<>();

        // find children of best attribute
        for (int i = 0; i < exampleIDs.size(); i++) {
            // get the attribute string at attribute id in this example
            String val = examples.get(i)[bestID];
            if (!newAttributes.contains(val)) {
                newAttributesCounts.add(0);
                newAttributes.add(val);
                ArrayList<Integer> nullList = new ArrayList<>();
                newAttributesIDs.add(nullList);
            }
        }

        for (int i = 0; i < newAttributes.size(); i++) {
            Node child = new Node();
            child.setValue(newAttributes.get(i));
            root.addChild(child);
            ArrayList<Integer> newExampleIDs = new ArrayList<>();
            for (int k = 0; k < exampleIDs.size(); k++) {
                Integer eid = exampleIDs.get(k);
                if (examples.get(eid)[bestID].equals(newAttributes.get(i))) {
                    newExampleIDs.add(eid);
                }
            }
            if (attributesIDs.size() != 0 && attributesIDs.contains(bestID)) {
                attributesIDs.remove(bestID);
            }
            // call DLT_ recursively with child
            child.setNext(DTL_(attributesIDs, newExampleIDs, child.getNext()));
        }

        return root;
    }

    public void print() {
        if (defaultN != null) {
            ArrayList<Node> nodes = new ArrayList<>();
            Node currN = new Node();
            nodes.add(defaultN);
            while (nodes.size() > 0) {
                currN = nodes.get(0);
                nodes.remove(0);
                if (currN.getValue() == null)
                    continue;

                System.out.println(currN.getValue());
                if (currN.hasChild()) {
                    for (Node c : currN.getChildren()) {
                        System.out.println(c.getValue());
                        nodes.add(c.getNext());
                    }
                }
            }
        }
    }

    private String mode(ArrayList<Integer> exampleIDs) {
        targetCts.clear();
        targetCts.add(0);
        targetCts.add(0);
        Integer yesCt = 0;
        Integer noCt = 0;
        for (int i = 0; i < exampleIDs.size(); i++) {
            if (targets.get(i).equals("Yes")) {
                yesCt++;
                targetCts.set(0, yesCt);
            } else {
                noCt++;
                targetCts.set(1, noCt);
            }
        }

        return targets.get(Collections.max(targetCts));
    }

    private Double getIGain(ArrayList<Integer> exampleIDs, Integer id) {
        ArrayList<String> attributes = new ArrayList<>();
        ArrayList<Integer> attributesCounts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> attributesIDs = new ArrayList<>();

        Double gain = getEntropy(exampleIDs);

        for (int i = 0; i < exampleIDs.size(); i++) {
            // get the attribute string at attribute id in this example
            Integer k = exampleIDs.get(i);
            String val = examples.get(k)[id];

            if (!attributes.contains(val)) {
                attributesCounts.add(0);
                attributes.add(val);
                ArrayList<Integer> nullList = new ArrayList<>();
                attributesIDs.add(nullList);
            }

            attributesIDs.get(attributes.indexOf(val)).add(k);
            int j = attributesCounts.get(attributes.indexOf(val));
            j++;
            attributesCounts.set(attributes.indexOf(val), j);
        }

        Integer attrCt = 0;
        ArrayList<Integer> attrID;

        for (int p = 0; p < attributesCounts.size(); p++) {
            attrCt = attributesCounts.get(p);
            attrID = attributesIDs.get(p);
            gain -= attrCt / Double.valueOf(exampleIDs.size()) * getEntropy(attrID);
        }

        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(gain));
    }

    private Integer chooseAttribute(ArrayList<Integer> attributesIDs, ArrayList<Integer> exampleIDs) {
        ArrayList<Double> attrEntropy = new ArrayList<>();

        System.out.print("\nAttributes=> [");
        for (Integer a : attributesIDs) {
            System.out.print(attributes.get(a) + ", ");
        }
        System.out.println("]");

        for (int i = 0; i < attributesIDs.size(); i++) {
            attrEntropy.add(i, getIGain(exampleIDs, attributesIDs.get(i)));
        }

        System.out.println("Information Gain=> " + attrEntropy);

        Integer maxID = attributesIDs.get(attrEntropy.indexOf(Collections.max(attrEntropy)));

        System.out.print("Highest Information Gain=> " + attributes.get(maxID));
        System.out.println(" (" + Collections.max(attrEntropy) + ")");
        System.out.println();

        return maxID;
    }

    private boolean sameAttrTarget(ArrayList<Integer> exampleIDs) {
        String t = targets.get(exampleIDs.get(0));

        for (int i = 1; i < exampleIDs.size(); i++) {
            Integer k = exampleIDs.get(i);
            if (!targets.get(k).equals(t))
                return false;
        }
        return true;
    }

    private Double getEntropy(ArrayList<Integer> exampleIDs) {
        ArrayList<Double> targetCts = new ArrayList<>();
        targetCts.add(0.0);
        targetCts.add(0.0);
        Double yesCount = 0.0;
        Double noCount = 0.0;
        Double entropy = 0.0;

        for (int i = 0; i < exampleIDs.size(); i++) {
            if (targets.get(exampleIDs.get(i)).equals("Yes")) {
                yesCount++;
                targetCts.set(0, yesCount);
            } else {
                noCount++;
                targetCts.set(1, noCount);
            }
        }

        for (Double ct : targetCts) {
            if (ct == 0)
                continue;
            else {
                entropy += -ct / exampleIDs.size() * log2(ct / exampleIDs.size());
            }
        }
        return entropy;
    }

    private Double log2(Double x) {
        return (Math.log(x) / Math.log(2));
    }

}
