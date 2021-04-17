package com.example.wegoeco;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Firebase {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    //private Frame frame;

    public void download(final String odo){
        ValueEventListener odolistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                frame = snapshot.child("message").getValue(Frame.class);
//                System.out.println(frame.getFrame());
                //odo = frame.getFrame();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.addListenerForSingleValueEvent(odolistener);
    }


    public void upload(Trip trip){
            myRef.push().setValue(trip);
    }
}
