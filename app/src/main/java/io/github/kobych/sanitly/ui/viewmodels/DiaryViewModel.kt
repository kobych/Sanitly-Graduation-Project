package io.github.kobych.sanitly.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.repositories.DiaryRepository
import io.github.kobych.sanitly.ui.models.DiaryStepsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DiaryStepsState>(DiaryStepsState.GoalSelect)
    val uiState: StateFlow<DiaryStepsState> = _uiState.asStateFlow()

    private val _goals = mutableStateOf<List<Goal>>(emptyList())
    var goals: State<List<Goal>> = _goals

    private val _selectedGoal = mutableStateOf<Goal?>(null)
    val selectedGoal: State<Goal?> = _selectedGoal

    private val _successNote = mutableStateOf("")
    val successNote: MutableState<String> = _successNote

    private val _failureNote = mutableStateOf("")
    val failureNote: MutableState<String> = _failureNote

    private val _summaryNote = mutableStateOf("")
    val summaryNote: MutableState<String> = _summaryNote

    fun canNoteExpand(goal: Goal): Boolean {
        return goal.successNote.isNotBlank() || goal.failureNote.isNotBlank() || goal.summaryNote.isNotBlank()
    }

    fun saveNotes() {
        val goal = _selectedGoal.value ?: return
        val isGoalCompleted = _selectedGoal.value?.isCompleted ?: false
        if (isGoalCompleted) {
            val updatedGoal = goal.copy(
                successNote = successNote.value,
                failureNote = failureNote.value,
                summaryNote = summaryNote.value
            )
            _selectedGoal.value = updatedGoal
            viewModelScope.launch {
                diaryRepository.saveNote(updatedGoal)
            }
        }
    }

    fun isButtonAvailable(): Boolean {
        return _selectedGoal.value?.isCompleted ?: false
    }

    fun updateDiaryState() {
        _uiState.update { currentState ->
            when (currentState) {
                DiaryStepsState.GoalSelect -> DiaryStepsState.GoalNote
                DiaryStepsState.GoalNote -> DiaryStepsState.GoalSelect
            }
        }
    }

    fun getAllGoals() {
        viewModelScope.launch {
            try {
                _goals.value = diaryRepository.getAllGoals()
            } catch (e: SQLiteException) {
                Log.e("DiaryViewModel", "Error fetching goals", e)
                _goals.value = emptyList()
            }
        }
    }

    fun selectGoal(goal: Goal) {
        _selectedGoal.value = goal
        _successNote.value = goal.successNote
        _failureNote.value = goal.failureNote
        _summaryNote.value = goal.summaryNote
    }
}
