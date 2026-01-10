package Service;

public interface Formattable {
    //defines how an object can present itself in different output formats
    String toDisplayString();    // For console output
    String toCSVString();        // For file output
    String toDetailedString();   // For detailed reports
}
