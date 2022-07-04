package masterthesis.conferences.server.controller;

public class ErrorFilter {
    private boolean errorFlag = false;

    public void detectError() {
        errorFlag = true;
    }

    public boolean getErrorFlag() {
        boolean status = errorFlag;
        this.errorFlag = false;
        return status;
    }
}
