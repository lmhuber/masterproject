package masterthesis.conferences;

public class ErrorFilter {
    private boolean errorFlag = false;

    public void detectError() {
        errorFlag = true;
    }

    /**
     * Util method to detect errors in elasticsearch flows for additional testing and debugging
     *
     * @return true if at least one error was raised, false otherwise
     */
    public boolean getErrorFlag() {
        boolean status = errorFlag;
        this.errorFlag = false;
        return status;
    }
}
