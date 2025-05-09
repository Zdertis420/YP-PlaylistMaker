package orc.zdertis420.playlistmaker.data

import orc.zdertis420.playlistmaker.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}