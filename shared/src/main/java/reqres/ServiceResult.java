package reqres;

public interface ServiceResult {
    default int getStatusCode(){
        return 200;
    }
}
