package orc.zdertis420.playlistmaker.data

import orc.zdertis420.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}