package reqres;

public record FailureResult(String message, int status) implements ServiceResult{
    @Override
    public int getStatusCode(){
        //this will be specified upon construction of the failure. 
        return status;
    }
}
