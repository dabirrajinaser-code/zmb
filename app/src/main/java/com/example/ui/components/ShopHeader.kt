package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AdminEntity
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua600
import com.example.ui.theme.WaterAqua800

@Composable
fun ShopHeaderCard(
    currentAdmin: AdminEntity?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LiquidGlossyCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logged in Admin badge at top right or top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WaterAqua100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Hatchery",
                            tint = WaterAqua800,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "মৎস্য বীজাগার ও খামার",
                        style = MaterialTheme.typography.labelMedium,
                        color = WaterAqua800,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Admin Info Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = WaterAqua100.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaterAqua600.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!currentAdmin?.photoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = currentAdmin?.photoUri,
                                contentDescription = "Admin Photo",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Admin Avatar",
                                tint = WaterAqua800,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentAdmin?.name ?: "এডমিন",
                            style = MaterialTheme.typography.labelSmall,
                            color = WaterAqua800,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Shop Name: "জিহান মৎস্য বীজাগার"
            Text(
                text = "জিহান মৎস্য বীজাগার",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = WaterAqua800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Address: "জগন্নাথপুর (হাজির বাজার), মোকামতলা, বগুড়া-৫৮১০"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Address",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "জগন্নাথপুর (হাজির বাজার), মোকামতলা, বগুড়া-৫৮১০",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Director Name: "পরিচালক: মোঃ ময়নুল সাদিক (জোহা)"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Director",
                    tint = WaterAqua800,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "পরিচালক: মোঃ ময়নুল সাদিক (জোহা)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fish Breed Descriptions (2 Lines, line 1 bigger, line 2 smaller)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = WaterAqua100.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "এখানে উন্নতমানের রুই, মৃগেল, কাতলা, বাটা, সীলভার, গ্লাসকাপ, স্বরপুটি,",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "পাঙ্গাস, হাংরি ,দেশী মাগুর , কৈ, টেংরা, গুলশা, পাবদা মাছের রেনু পোনা পাওয়া যায়",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF37474F),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Phone Numbers: +8801722736633, +8801711428073
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhoneBadge(phone = "+8801722736633", onCall = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+8801722736633"))
                    context.startActivity(intent)
                })
                Spacer(modifier = Modifier.width(10.dp))
                PhoneBadge(phone = "+8801711428073", onCall = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+8801711428073"))
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun PhoneBadge(
    phone: String,
    onCall: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = OceanBlue500.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, OceanBlue500.copy(alpha = 0.3f)),
        modifier = Modifier.clickable { onCall() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = OceanBlue500,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = phone,
                style = MaterialTheme.typography.labelSmall,
                color = OceanBlue500,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
