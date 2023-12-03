package kz.kbtu.sxodimkbtu.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import kz.kbtu.sxodimkbtu.model.Department;
import kz.kbtu.sxodimkbtu.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class DepartmentService {

    public static final String COL_NAME="departments";
    //
    public List<Department> getDepartments() throws InterruptedException, ExecutionException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = dbFireStore.collection(COL_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Department> departments = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            departments.add(document.toObject(Department.class));
        }
        return departments;
    }
    //Create a doc in FireStore
    public String saveDepartmentDetails(Department department) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve the last department ID
        Query lastEventQuery = dbFirestore.collection(COL_NAME).orderBy("id", Query.Direction.DESCENDING).limit(1);
        ApiFuture<QuerySnapshot> lastEventFuture = lastEventQuery.get();
        QuerySnapshot lastEventSnapshot = lastEventFuture.get();

        Long lastDepartmentId = null;
        if (!lastEventSnapshot.isEmpty()) {
            DocumentSnapshot lastEventDocument = lastEventSnapshot.getDocuments().get(0);
            lastDepartmentId = lastEventDocument.getLong("id");
            log.info(String.valueOf(lastDepartmentId));
        }

        // Generate the new department ID
        Long newDepartmentId = lastDepartmentId == null ? 1 : lastDepartmentId + 1;

        // Update the event object with the new ID
        department.setDepartmentID(newDepartmentId);

        // Add the department to Firestore
        ApiFuture<WriteResult> collectionsApiFuture =
                dbFirestore.collection(COL_NAME).document(department.getDepartmentName()).set(department);

        return collectionsApiFuture.get().getUpdateTime().toString();
    }
}
