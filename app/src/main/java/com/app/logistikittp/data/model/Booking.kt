package com.app.logistikittp.data.model

data class Booking(
    val idBooking: String? = null,
    val userId : String? = null,
    val gedung: String? = null,
    val ruangan: String? = null,
    val tanggal_pinjam: String? = null,
    val waktu_pinjam: String? = null,
    val status: String? = null,
    val peminjam: String? = null,
    val asal_peminjam: String? = null,
    val nim_nik: String? = null,
    val nomor_hp: String? = null,
    val keterangan_acara: String? = null,
    val surat_izin: String? = null
)