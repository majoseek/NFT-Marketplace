package com.example.nftmarketplace.nft.data

import com.example.nftmarketplace.core.data.NFTDomainModel
import com.example.nftmarketplace.nft.storage.db.NFTEntity

object FileExtension {
    const val JPG = "jpg"
    const val PNG = "png"
    const val JPEG = "jpeg"
    const val GIF = "gif"
    const val BMP = "bmp"
    const val WEBP = "webp"
    const val SVG = "svg"

    const val MP4 = "mp4"
    const val AVI = "avi"
    const val MOV = "mov"
    const val FLV = "flv"
    const val WMV = "wmv"

    const val MP3 = "mp3"
    const val WAV = "wav"
    const val FLAC = "flac"
    const val AAC = "aac"

    const val TXT = "txt"
    const val DOC = "doc"
    const val DOCX = "docx"
    const val PDF = "pdf"

    fun getTypeFromExtension(extension: String?) = when(extension) {
        JPG, PNG, JPEG, GIF, BMP, SVG, WEBP -> NFTDomainModel.Type.Image
        MP4, AVI, MOV, FLV, WMV -> NFTDomainModel.Type.Video
        MP3, WAV, FLAC, AAC -> NFTDomainModel.Type.Audio
        TXT, DOC, DOCX, PDF -> NFTDomainModel.Type.Text
        else -> NFTDomainModel.Type.Other
    }

    fun getNFTEntityTypeFromExtension(extension: String?) = when(extension) {
        JPG, PNG, JPEG, GIF, BMP, SVG, WEBP -> NFTEntity.Type.Image
        MP4, AVI, MOV, FLV, WMV -> NFTEntity.Type.Video
        MP3, WAV, FLAC, AAC -> NFTEntity.Type.Audio
        TXT, DOC, DOCX, PDF -> NFTEntity.Type.Text
        else -> NFTEntity.Type.Other
    }
}
