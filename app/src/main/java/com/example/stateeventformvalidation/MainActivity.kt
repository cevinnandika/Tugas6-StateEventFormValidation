package com.example.stateeventformvalidation

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stateeventformvalidation.ui.theme.StateEventFormValidationTheme

/**
 * Activity utama aplikasi yang menjadi titik masuk (entry point).
 * Mengatur palet warna tema kustom (Emerald & Teal) dan menampilkan [RegistrationScreen].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Modifikasi Tugas Individu: Palet warna baru (Emerald Green & Deep Slate)
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0F766E),     // Teal / Emerald
                    onPrimary = Color.White,
                    secondary = Color(0xFFD97706),   // Amber Accent
                    background = Color(0xFFF0FDF4),  // Soft Mint Background
                    surface = Color.White,
                    error = Color(0xFFDC2626)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistrationScreen()
                }
            }
        }
    }
}

// =========================================================================
// FUNGSI HELPER VALIDASI
// Memisahkan logika aturan validasi dari UI agar lebih rapi dan mudah diuji.
// =========================================================================

fun isNameValid(nama: String): Boolean = nama.trim().length >= 3

fun isNimValid(nim: String): Boolean =
    nim.trim().length in 8..12 && nim.trim().all { it.isDigit() }

fun isPhoneValid(telepon: String): Boolean =
    telepon.trim().length in 10..13 && telepon.trim().all { it.isDigit() }

fun isEmailValid(email: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

fun isPasswordValid(password: String): Boolean =
    password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }

fun isConfirmationValid(password: String, confirmation: String): Boolean =
    confirmation.isNotEmpty() && password == confirmation

// =========================================================================
// KOMPONEN REUSABLE DENGAN STATE HOISTING
// Komponen child menerima nilai melalui parameter (value) dan melaporkan
// perubahan ke pemilik state melalui callback (onValueChange).
// =========================================================================

@Composable
fun FormInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isError: Boolean,
    errorText: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = {
            if (isError) {
                Text(errorText)
            }
        }
    )
}

/**
 * Screen-level state owner untuk formulir Pendaftaran Anggota Perpustakaan Digital (SIMPUS UMSIDA).
 * Memperagakan penerapan State, Event, State Hoisting, Anatomi OutlinedTextField, dan Form Validation.
 */
@Composable
fun RegistrationScreen() {
    // =========================================================================
    // 1. STATE MANAGEMENT
    // =========================================================================

    // rememberSaveable: Bertahan saat recomposition maupun rotasi layar (konfigurasi berubah)
    var nama by rememberSaveable { mutableStateOf("") }
    var nim by rememberSaveable { mutableStateOf("") }         // Field tambahan 1: NIM Mahasiswa
    var nomorWa by rememberSaveable { mutableStateOf("") }     // Field tambahan 2: Nomor WhatsApp
    var email by rememberSaveable { mutableStateOf("") }
    var setuju by rememberSaveable { mutableStateOf(false) }
    var passwordTerlihat by rememberSaveable { mutableStateOf(false) }
    var konfirmasiTerlihat by rememberSaveable { mutableStateOf(false) }

    // remember: State sementara (transient) untuk keamanan kata sandi agar tidak disimpan di Bundle
    var password by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }

    // State status pengiriman dan hasil validasi
    var sudahSubmit by rememberSaveable { mutableStateOf(false) }
    var berhasil by rememberSaveable { mutableStateOf(false) }

    // =========================================================================
    // 2. FORM VALIDATION (VALIDASI FORMULIR)
    // =========================================================================

    val errorNama = sudahSubmit && !isNameValid(nama)
    val errorNim = sudahSubmit && !isNimValid(nim)
    val errorNomorWa = sudahSubmit && !isPhoneValid(nomorWa)
    val errorEmail = sudahSubmit && !isEmailValid(email)
    val errorPassword = sudahSubmit && !isPasswordValid(password)
    val errorKonfirmasi = sudahSubmit && !isConfirmationValid(password, konfirmasiPassword)
    val errorPersetujuan = sudahSubmit && !setuju

    // Seluruh formulir dinyatakan valid jika semua aturan terpenuhi
    val formValid = isNameValid(nama) &&
            isNimValid(nim) &&
            isPhoneValid(nomorWa) &&
            isEmailValid(email) &&
            isPasswordValid(password) &&
            isConfirmationValid(password, konfirmasiPassword) &&
            setuju

    // Layout utama layar dengan dukungan scroll vertikal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Aplikasi (Tema Baru: SIMPUS Digital UMSIDA)
        Text(
            text = "SIMPUS Digital UMSIDA",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Registrasi Anggota Perpustakaan",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Lengkapi data mahasiswa di bawah ini untuk mengaktifkan akses e-Library.",
            color = Color(0xFF475569),
            fontSize = 14.sp
        )

        // Kartu kontainer formulir
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Field 1: Nama Lengkap Mahasiswa (Menggunakan State Hoisting)
                FormInputField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        berhasil = false
                    },
                    label = "Nama lengkap mahasiswa",
                    placeholder = "Contoh: Alya Putri",
                    leadingIcon = Icons.Filled.Person,
                    isError = errorNama,
                    errorText = "Nama wajib diisi (minimal 3 karakter)"
                )

                // Field 2 (Tambahan): Nomor Induk Mahasiswa (NIM)
                FormInputField(
                    value = nim,
                    onValueChange = {
                        nim = it
                        berhasil = false
                    },
                    label = "Nomor Induk Mahasiswa (NIM)",
                    placeholder = "Contoh: 221080200012",
                    leadingIcon = Icons.Filled.Badge,
                    keyboardType = KeyboardType.Number,
                    isError = errorNim,
                    errorText = "NIM harus berupa angka (8–12 digit)"
                )

                // Field 3 (Tambahan): Nomor WhatsApp Aktif
                FormInputField(
                    value = nomorWa,
                    onValueChange = {
                        nomorWa = it
                        berhasil = false
                    },
                    label = "Nomor WhatsApp aktif",
                    placeholder = "Contoh: 081234567890",
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone,
                    isError = errorNomorWa,
                    errorText = "Nomor WhatsApp harus angka (10–13 digit)"
                )

                // Field 4: Email Kampus / Pribadi
                FormInputField(
                    value = email,
                    onValueChange = {
                        email = it
                        berhasil = false
                    },
                    label = "Email mahasiswa",
                    placeholder = "nama@umsida.ac.id",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    isError = errorEmail,
                    errorText = "Masukkan format alamat email yang valid"
                )

                // Field 5: Kata Sandi (dengan aturan kombinasi huruf & angka)
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kata sandi akun") },
                    placeholder = { Text("Min. 8 karakter (huruf & angka)") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordTerlihat = !passwordTerlihat }) {
                            Icon(
                                imageVector = if (passwordTerlihat) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordTerlihat) "Sembunyikan kata sandi" else "Tampilkan kata sandi"
                            )
                        }
                    },
                    visualTransformation = if (passwordTerlihat) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = errorPassword,
                    supportingText = {
                        if (errorPassword) {
                            Text("Kata sandi minimal 8 karakter serta wajib kombinasi huruf dan angka")
                        }
                    }
                )

                // Field 6: Konfirmasi Kata Sandi
                OutlinedTextField(
                    value = konfirmasiPassword,
                    onValueChange = {
                        konfirmasiPassword = it
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Konfirmasi kata sandi") },
                    placeholder = { Text("Ketik ulang kata sandi") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { konfirmasiTerlihat = !konfirmasiTerlihat }) {
                            Icon(
                                imageVector = if (konfirmasiTerlihat) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (konfirmasiTerlihat) "Sembunyikan konfirmasi" else "Tampilkan konfirmasi"
                            )
                        }
                    },
                    visualTransformation = if (konfirmasiTerlihat) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = errorKonfirmasi,
                    supportingText = {
                        if (errorKonfirmasi) {
                            Text("Konfirmasi kata sandi harus sama dengan kata sandi")
                        }
                    }
                )

                // Checkbox Persetujuan Tata Tertib Perpustakaan
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = setuju,
                        onCheckedChange = {
                            setuju = it
                            berhasil = false
                        }
                    )
                    Text(
                        text = "Saya menyetujui tata tertib peminjaman koleksi perpustakaan",
                        fontSize = 13.sp
                    )
                }
                if (errorPersetujuan) {
                    Text(
                        text = "Persetujuan tata tertib wajib dicentang",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                // =========================================================================
                // 3. EVENT SUBMIT FORMULIR
                // =========================================================================
                Button(
                    onClick = {
                        sudahSubmit = true
                        berhasil = formValid
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Aktifkan Keanggotaan",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Tampilan Pesan Sukses jika semua aturan validasi terpenuhi
                if (berhasil) {
                    HorizontalDivider(color = Color(0xFFA7F3D0))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669)
                        )
                        Column {
                            Text(
                                text = "Registrasi Berhasil!",
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Halo, ${nama.trim()} (NIM: ${nim.trim()})! Kartu anggota digital Anda siap digunakan.",
                                color = Color(0xFF1E293B),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Petunjuk: Pastikan NIM (8–12 digit angka), No. WA (10–13 digit), email valid, dan kata sandi kombinasi huruf & angka.",
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    StateEventFormValidationTheme {
        RegistrationScreen()
    }
}