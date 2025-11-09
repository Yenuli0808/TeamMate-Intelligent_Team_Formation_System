package Service;

public interface Formattable {
    String toDisplayString();    // For console output
    String toCSVString();        // For file output
    String toDetailedString();   // For detailed reports
}
