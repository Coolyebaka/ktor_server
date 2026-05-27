package com.huntersdiary.notes.di

import com.huntersdiary.notes.data.FirestoreNoteRepository
import com.huntersdiary.notes.domain.CreateNoteUseCase
import com.huntersdiary.notes.domain.DeleteNoteUseCase
import com.huntersdiary.notes.domain.GetNoteByIdUseCase
import com.huntersdiary.notes.domain.GetNotesUseCase
import com.huntersdiary.notes.domain.NoteRepository
import com.huntersdiary.notes.domain.UpdateNoteUseCase
import org.koin.dsl.module

val notesModule = module {
    single<NoteRepository> { FirestoreNoteRepository(get()) }
    single { CreateNoteUseCase(get()) }
    single { GetNotesUseCase(get()) }
    single { GetNoteByIdUseCase(get()) }
    single { UpdateNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
}
