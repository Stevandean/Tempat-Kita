import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    public static void addUser(Firestore db, String userId, String username, String email, String fullName) throws Exception {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("fullName", fullName);
        userData.put("createdAt", FieldValue.serverTimestamp());

        ApiFuture<WriteResult> result = db.collection("users").document(userId).set(userData);
        System.out.println("User added at: " + result.get().getUpdateTime());
    }
}
