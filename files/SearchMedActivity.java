package com.beautifulten.doctor.ui.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import com.beautifulten.doctor.R;
import com.beautifulten.doctor.bean.Medicine;
import com.beautifulten.doctor.ui.AbsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mars on 2015/8/27.
 */
public class SearchMedActivity extends AbsActivity {

    @Bind(R.id.meds_rv)
    RecyclerView mMedsRv;
    @Bind(R.id.search_content)
    EditText mSearchContent;
    private MedsAdapter mMedsAdapter;
    private ArrayList<Medicine> mMedicines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_search_med_activity);
        addDrawable = getResources().getDrawable(R.drawable.order_search_med_add);
        mMedsRv.addItemDecoration(new MedItemDecoration(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mMedsRv.setLayoutManager(linearLayoutManager);
        mMedicines = new ArrayList<>();
        updateSearchMedsByNet();
        updateSearchMedsByAddedMeds();
        mMedsAdapter = new MedsAdapter(this, mMedicines);
        mMedsRv.setAdapter(mMedsAdapter);
        mSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = s.toString();
                //todo get data from net then update list
                updateSearchMedsByNet();
                updateSearchMedsByAddedMeds();
                mMedsAdapter.notifyDataSetChanged();
            }
        });
    }
    void updateSearchMedsByNet(){
        mMedicines.add(new Medicine("广誉远 定坤丹", "(10.8g*6丸)","遵循药品说明","遵循药品说明",1,320f,false));
        mMedicines.add(new Medicine("同仁堂 乌鸡白凤丸", "(9g*10丸)","口服","一天三次 每次5丸",1,320f,false));
        mMedicines.add(new Medicine("牌子1 药1", "(9g*10丸)","遵循药品说明","遵循药品说明",1,60f, false));
        mMedicines.add(new Medicine("牌子2 药2", "(9g*10丸)","遵循药品说明","遵循药品说明",1,60f, false));
        mMedicines.add(new Medicine("牌子3 药3", "(9g*10丸)","遵循药品说明","遵循药品说明",1,60f, false));
        mMedicines.add(new Medicine("牌子4 药4", "(9g*10丸)","遵循药品说明","遵循药品说明",1,60f, false));
    }
    void updateSearchMedsByAddedMeds(){
        ArrayList<Medicine> adds = getIntent().getParcelableArrayListExtra(OrderActivity.key_added_meds);
        for (Medicine added : adds){
            for (Medicine med: mMedicines){
                if (added.equals(med)){
                    med.mAdded = true;
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.order_search_med_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        switch (item.getItemId()) {
            case R.id.complete_item:
                result = true;
                if (mResultList.size() > 0) {
                    mResultIntent.putParcelableArrayListExtra(key_add, mResultList);
                    setResult(result_code_add, mResultIntent);
                } else {
                    setResult(result_code_not_add);
                }
                finish();
                break;
        }
        if (!result) {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    public static final int result_code_add = 0;
    public static final int result_code_not_add = 1;

    static Drawable addDrawable;

    class MedsHolder extends RecyclerView.ViewHolder {
        TextView mItem;

        public MedsHolder(View itemView) {
            super(itemView);
            mItem = (TextView) itemView;
        }

        public void onBind(List<Medicine> medicines, int i) {
            Medicine medicine = medicines.get(i);
            mItem.setText(String.format("%s %s", medicine.mName, medicine.mStandard));
            mItem.setCompoundDrawablesWithIntrinsicBounds(null, null, medicine.mAdded ? null :
                    addDrawable, null);
            if (!medicine.mAdded) {
                mItem.setOnClickListener(mOnItemClickListener);
            }
        }
    }
    public static final String key_add = "add";
    Intent mResultIntent = new Intent();
    ArrayList<Medicine> mResultList = new ArrayList();
    View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int adapterPosition = mMedsRv.getChildAdapterPosition(v);
            mResultList.add(mMedicines.get(adapterPosition));
            mMedicines.get(adapterPosition).mAdded = true;
            mMedsAdapter.notifyItemChanged(adapterPosition);
        }
    };


    class MedsAdapter extends RecyclerView.Adapter<MedsHolder> {
        Context mContext;
        List<Medicine> mMedicines;

        public MedsAdapter(Context context, List<Medicine> medicines) {
            mContext = context;
            mMedicines = medicines;
        }

        @Override
        public MedsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout
                    .order_search_med_list_item, viewGroup, false);
            return new MedsHolder(view);
        }

        @Override
        public void onBindViewHolder(MedsHolder medsHolder, int i) {
            medsHolder.onBind(mMedicines, i);
        }

        @Override
        public int getItemCount() {
            return mMedicines.size();
        }
    }

}
