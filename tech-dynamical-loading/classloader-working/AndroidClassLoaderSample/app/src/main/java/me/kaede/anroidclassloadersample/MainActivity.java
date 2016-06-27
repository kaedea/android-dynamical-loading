package me.kaede.anroidclassloadersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	ActivityHolder activityHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recyclerview);

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
		activityHolder = new ActivityHolder();
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		MyAdapter adapter = new MyAdapter();;
		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

		activityHolder.addActivity("Get All ClassLoader", GetAllClassLoaderActivity.class);
		adapter.notifyDataSetChanged();
	}

	public class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_samplelist_recycleview, parent, false);
			ViewHolder vh = new ViewHolder(v);
			return vh;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, final int position) {
			holder.textView.setText(activityHolder.getNameList().get(position));
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activityHolder.startActivity(v.getContext(),position);
				}
			});
		}

		@Override
		public int getItemCount() {
			return activityHolder.getNameList().size();
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder{
		public TextView textView;
		public ViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.tv_item_samplelist_recyclerview);
		}
	}
}
