package edu.neu.madcourse.buoy;/*
Sources:
https://developersbreach.com/merge-multiple-adapters-with-concatadapter-android/
https://medium.com/swlh/expandable-list-in-android-with-mergeadapter-3a7f0cb56166
https://medium.com/androiddevelopers/merge-adapters-sequentially-with-mergeadapter-294d2942127a
https://developer.android.com/guide/topics/ui/controls/checkbox
https://medium.com/techmacademy/how-to-read-and-write-booleans-in-a-parcelable-class-99e5948db58d

 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.neu.madcourse.buoy.InnerAdapter;
import edu.neu.madcourse.buoy.InnerItemCard;
import edu.neu.madcourse.buoy.ItemAdapter;
import edu.neu.madcourse.buoy.ItemCard;
import edu.neu.madcourse.buoy.R;

public class userList extends AppCompatActivity {
    private ArrayList<ItemCard> itemCardArrayList =new ArrayList<>(); //item card list
    static final String ITEMCARDLIST = "itemCardList";
    private ConcatAdapter concatAdapter; //main adapter merges all adapters together.

    private ArrayList<ItemAdapter> parentAdapters; //list of item card adapters
    static final String PARENTADAPTERLIST = "parentAdapterList";
    private HashMap<ItemCard, InnerAdapter> innerAdapters; //list of inner lists mapped to item card as keys

    private RecyclerView recyclerView;
    Button btnSubmit;
    Button btnAddTodo;
    EditText newTodoInput;
    EditText newListInput;
    private String uid;//this user's id

    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        btnSubmit = findViewById(R.id.userListSubmit);
        btnAddTodo = findViewById(R.id.checkBoxAdd);
        newTodoInput = findViewById(R.id.checkBoxInput);
        newListInput = findViewById(R.id.userListInput);
        mdataBase = FirebaseDatabase.getInstance().getReference();
        //mockList();
        uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                itemCardArrayList = user.getItemCardArrayList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        createRecyclerView();

        this.parentAdapters = new ArrayList<>();
        for (int i = 0; i < this.itemCardArrayList.size(); i++) {
            ItemAdapter item = new ItemAdapter(this.itemCardArrayList.get(i));
            this.parentAdapters.add(item);
        }
        setAdapters();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newListInput.getText().toString();
                if (name.isEmpty()) {
                    newListInput.setError("List must have a name");
                } else if (name.length() > 15) {
                    newListInput.setError("List name is too long");
                } else {
                    newList(name);
                    newListInput.setText("");

                    //close the keyboard
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // fine nevermind then I didn't want you to have fun anyway
                    }
                }
            }
        });
        for (ItemAdapter parent : parentAdapters){
            parent.notifyDataSetChanged();
        }
    }

    private void setAdapters() {
        for (int i = 0; i < this.parentAdapters.size(); i++) {
            ItemAdapter item = this.parentAdapters.get(i);
            this.concatAdapter.addAdapter(item);
            int finalI = i;

            item.setOnItemClickListener(new ItemAdapter.ItemClickListener() {
                @Override
                public synchronized void onItemClick() {
                    ItemCard itemCard = itemCardArrayList.get(finalI);
                    if (itemCard.isExpanded()) { //if inner list is already expanded, tap should close list.
                        concatAdapter.removeAdapter(Objects.requireNonNull(innerAdapters.get(itemCard)));
                        itemCard.setExpanded();
                    } else { //else expand list.
                        itemCard.setExpanded(); //set expanded to true
                        concatAdapterSet(); //show concat adapter list
                    }
                    item.notifyDataSetChanged();
                }

                @Override
                public synchronized void onDeletePressed() {
                    ItemCard card = itemCardArrayList.get(finalI);
                    parentAdapters.remove(item);
                    innerAdapters.remove(card);
                    concatAdapter.removeAdapter(item);
                    concatAdapterSet();
                    mdataBase.child("itemCardArrayList").setValue(itemCardArrayList);
                    item.notifyDataSetChanged();
                }

                @Override
                public synchronized void onTodoAddPressed(String header, String todo) {
                    ItemCard card = null;
                    for (int i = 0; i<itemCardArrayList.size(); i ++){
                        if (itemCardArrayList.get(i).getHeader().equals(header)){
                            card = itemCardArrayList.get(i);
                            break;
                        }
                    }
                    if (card == null) {
                        Toast.makeText(userList.this, "error making Todo", Toast.LENGTH_SHORT).show();
                    } else {
                       card.getHeaderList().add(new InnerItemCard(todo));
                       mdataBase.child("itemCardArrayList").setValue(itemCardArrayList);
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // fine nevermind then I didn't want you to have fun anyway
                        }


                    }

                }

            });
        }
        populateInnerAdapterList();
    }

    public void newList(String name) {
        ArrayList<InnerItemCard> newList = new ArrayList<>();
        newList.add(new InnerItemCard("Make my new List!"));
        ItemCard newCard = new ItemCard(name, newList);
        InnerAdapter newInner = new InnerAdapter(newList);
        itemCardArrayList.add(newCard);
        int listnum = itemCardArrayList.size() - 1;
        ItemAdapter item = new ItemAdapter(this.itemCardArrayList.get(listnum));
        this.parentAdapters.add(item);
        this.innerAdapters.put(newCard, newInner);
        mdataBase.child("itemCardArrayList").setValue(itemCardArrayList);
        setAdapters();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(ITEMCARDLIST, this.itemCardArrayList);
        savedInstanceState.putSerializable(PARENTADAPTERLIST, this.parentAdapters);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        itemCardArrayList = (ArrayList<ItemCard>) savedInstanceState.getSerializable(ITEMCARDLIST);
        parentAdapters = (ArrayList<ItemAdapter>) savedInstanceState.getSerializable(PARENTADAPTERLIST);
        createRecyclerView();

        setAdapters();
    }

    private void createRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_nested);
        recyclerView.setHasFixedSize(true);

        this.concatAdapter = new ConcatAdapter();
        recyclerView.setAdapter(this.concatAdapter);

        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);
    }


    //populate nested lists of parent.
    private void populateInnerAdapterList() {
        this.innerAdapters = new HashMap<>();
        for (int i = 0; i < this.itemCardArrayList.size(); i++) {
            ArrayList<InnerItemCard> list = this.itemCardArrayList.get(i).getHeaderList();
            InnerAdapter adapter = new InnerAdapter(list);
            this.innerAdapters.put(this.itemCardArrayList.get(i), adapter);
            adapter.setOnInnerClickListener(new InnerAdapter.InnerItemClickListener() {
                @Override
                public void onItemClick(int pos) {
                    //some action happens here.
                }

                @Override
                public void onCheckClick(int pos) {
                    list.get(pos).setChecked();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void mockList() {

        this.itemCardArrayList = new ArrayList<>();
        //Mock out nested list of item cards to show
        ArrayList<InnerItemCard> list1 = new ArrayList<>();
        ArrayList<InnerItemCard> list2 = new ArrayList<>();
        ArrayList<InnerItemCard> list3 = new ArrayList<>();

        list1.add(new InnerItemCard("list 1 item 1"));
        list1.add(new InnerItemCard("list 1 item 2"));
        list1.add(new InnerItemCard("list 1 item 3"));


        list2.add(new InnerItemCard("list 2 item 1"));
        list2.add(new InnerItemCard("list 2 item 2"));

        itemCardArrayList.add(new ItemCard("list1", list1));
        itemCardArrayList.add(new ItemCard("list2", list2));
        itemCardArrayList.add(new ItemCard("list3", list3));

    }
    //concat needs to be sequential.
    private void concatAdapterSet() {
        for (Map.Entry<ItemCard, InnerAdapter> each : this.innerAdapters.entrySet()) {
            concatAdapter.removeAdapter(each.getValue());
        }
        int concatIndex = 0;
        for (int i = 0; i < this.parentAdapters.size(); i++) {
            ItemCard currentParent = this.itemCardArrayList.get(i);
            concatIndex++;
            if (currentParent.isExpanded()) {
                concatAdapter.addAdapter(concatIndex, Objects.requireNonNull(this.innerAdapters.get(currentParent)));
                concatIndex++;
            }
        }
    }


}