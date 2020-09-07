import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class StartDTL {
    ArrayList<String[]> examples = new ArrayList<>();
    ArrayList<String> attributes = new ArrayList<>();
    ArrayList<String> targets = new ArrayList<>();
    public StartDTL() {
        initializeAttributes();
        initializeFile();
        DTL tree = new DTL(examples, attributes, targets);
        tree.DTL();
        tree.print();
    }
    private void initializeAttributes () {
        attributes.add("Alt");
        attributes.add("Bar");
        attributes.add("Fri");
        attributes.add("Hun");
        attributes.add("Pat");
        attributes.add("Price");
        attributes.add("Rain");
        attributes.add("Res");
        attributes.add("Type");
        attributes.add("Est");
    }

    private void initializeFile () {
        URL url = getClass().getResource("/resources/resteraunt.csv");
        File resourceFile = null;

        try {
            URI uri = url.toURI();
            resourceFile = new File(uri);
        } catch (URISyntaxException error) {
            error.printStackTrace();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(resourceFile));
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                fileLine = fileLine.replaceAll("\\s", "");
                String[] lineSplit = fileLine.split(",");
                targets.add(lineSplit[lineSplit.length-1]);

                String[] exLine = new String[lineSplit.length-1];
                for (int i = 0 ; i < lineSplit.length -1 ; i++) {
                    exLine[i] = lineSplit[i];
                }
                examples.add(exLine);
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
