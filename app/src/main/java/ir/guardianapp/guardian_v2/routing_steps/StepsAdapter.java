package ir.guardianapp.guardian_v2.routing_steps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.guardianapp.guardian_v2.OSRM.RoutingHelper;
import ir.guardianapp.guardian_v2.OSRM.Step;
import ir.guardianapp.guardian_v2.R;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.TripViewHolder> {
    private List<Step> steps;
    Context context;

    public StepsAdapter(List<Step> steps, Context context) {
        this.steps = steps;
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_row_item,parent,false);
        return new TripViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Step step = steps.get(position);
        if(step.getName().equalsIgnoreCase(""))
            holder.streetName.setText("خیابان");
        else
            holder.streetName.setText(step.getName());
        holder.stepInstruction.setText(RoutingHelper.getStepText(step, 0));
        holder.stepIcon.setImageResource(RoutingHelper.getStepImage(step));
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        private TextView streetName;
        private TextView stepInstruction;
        private ImageView stepIcon;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
//            button.setOnClickListener(this);
            streetName = itemView.findViewById(R.id.street_name);
            stepInstruction = itemView.findViewById(R.id.stepInstruction);
            stepIcon = itemView.findViewById(R.id.stepIcon);
        }

    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}