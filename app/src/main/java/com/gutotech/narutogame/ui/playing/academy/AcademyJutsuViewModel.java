package com.gutotech.narutogame.ui.playing.academy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.model.CharOn;
import com.gutotech.narutogame.data.model.Classe;
import com.gutotech.narutogame.data.model.Formulas;
import com.gutotech.narutogame.data.model.Jutsu;
import com.gutotech.narutogame.data.repository.CharacterRepository;
import com.gutotech.narutogame.data.repository.JutsuRepository;
import com.gutotech.narutogame.ui.adapter.JutsusLearnAdapter;
import com.gutotech.narutogame.utils.SingleLiveEvent;

import java.util.List;

public class AcademyJutsuViewModel extends ViewModel
        implements JutsusLearnAdapter.OnTrainClickListener {
    private MutableLiveData<List<Jutsu>> mJutsus = new MutableLiveData<>();

    private MutableLiveData<Classe> mClassSelected;

    private SingleLiveEvent<Integer> mShowCongratulationsEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mShowWarningEvent = new SingleLiveEvent<>();

    private JutsuRepository mJutsuRepository = JutsuRepository.getInstance();

    public AcademyJutsuViewModel() {
        mClassSelected = new MutableLiveData<>(CharOn.character.getClasse());
        filterJutsus();
    }

    public void onClassButtonPressed(Classe classe) {
        mClassSelected.setValue(classe);
        filterJutsus();
    }

    private void filterJutsus() {
        mJutsuRepository.filterJutsus(mClassSelected.getValue(), mJutsus::postValue);
    }

    @Override
    public synchronized void onTrainClick(Jutsu jutsu) {
        Formulas formulas = CharOn.character.getAttributes().getFormulas();

        if (formulas.getCurrentChakra() >= jutsu.getConsumesChakra() * 2) {
            if (formulas.getCurrentStamina() >= jutsu.getConsumesStamina() * 2) {
                formulas.subChakra(jutsu.getConsumesChakra() * 2);
                formulas.subStamina(jutsu.getConsumesStamina() * 2);

                CharOn.character.getJutsus().add(jutsu);
                mJutsuRepository.sort(CharOn.character.getJutsus());
                CharacterRepository.getInstance().save(CharOn.character);

                mShowCongratulationsEvent.setValue(jutsu.getJutsuInfo().name);
                filterJutsus();
            } else {
                mShowWarningEvent.setValue(R.string.stamina);
            }
        } else {
            mShowWarningEvent.setValue(R.string.chakra);
        }
    }

    LiveData<List<Jutsu>> getJutsus() {
        return mJutsus;
    }

    public LiveData<Classe> getClassSelected() {
        return mClassSelected;
    }

    LiveData<Integer> getShowCongratulationsEvent() {
        return mShowCongratulationsEvent;
    }

    LiveData<Integer> getShowWarningEvent() {
        return mShowWarningEvent;
    }
}
