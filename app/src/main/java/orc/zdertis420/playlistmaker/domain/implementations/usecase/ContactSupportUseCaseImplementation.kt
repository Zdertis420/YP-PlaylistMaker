package orc.zdertis420.playlistmaker.domain.implementations.usecase

import android.content.Intent
import orc.zdertis420.playlistmaker.domain.repository.ContactSupportRepository
import orc.zdertis420.playlistmaker.domain.usecase.ContactSupportUseCase

class ContactSupportUseCaseImplementation(private val contactSupportRepository: ContactSupportRepository) : ContactSupportUseCase {
    override fun contactSupport(): Intent {
        return contactSupportRepository.contactSupport()
    }
}