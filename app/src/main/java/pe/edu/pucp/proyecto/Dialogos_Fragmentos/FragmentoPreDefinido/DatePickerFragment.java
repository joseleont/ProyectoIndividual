package pe.edu.pucp.proyecto.Dialogos_Fragmentos.FragmentoPreDefinido;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;

import java.util.Calendar;

import pe.edu.pucp.proyecto.Vendedor.NuevaDeuda;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        NuevaDeuda nuevaDeuda= (NuevaDeuda) getActivity();
        nuevaDeuda.respuestaDatePicker(year,month,day);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        // El objeto calendar nos permite tenr la fecha y hora actual
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);


    }
}