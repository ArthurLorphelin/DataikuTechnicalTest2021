import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException, ParseException {
        File milleniumFalconJsonFilePath = new File("/Users/arthur/IdeaProjects/DataikuTechnicalTest/src/milleniumFalconJsonFilePath.json");
        File empireJsonFile = new File("/Users/arthur/IdeaProjects/DataikuTechnicalTest/src/empireJsonFile.json");

        C3PO c3PO = new C3PO(milleniumFalconJsonFilePath);
        double maximalOddsOfSuccess = c3PO.giveMeTheOdds(empireJsonFile);
        System.out.println(maximalOddsOfSuccess);
    }
}
