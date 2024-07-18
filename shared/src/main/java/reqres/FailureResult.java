package reqres;

public record FailureResult(String message) implements ServiceResult{
//    @Override
//    public int getStatusCode(){
//        //this will be specified upon construction of the failure.
//        return status;
//    }
}
