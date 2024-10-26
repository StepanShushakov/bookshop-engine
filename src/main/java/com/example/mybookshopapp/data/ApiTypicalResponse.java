package com.example.mybookshopapp.data;

/**
 * @author karl
 */

public class ApiTypicalResponse {

    private boolean result;
    private String error;

    public ApiTypicalResponse() {
        this.result = true;
    }

    public ApiTypicalResponse(boolean result, String error) {
        this.result = result;
        this.error = error;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
