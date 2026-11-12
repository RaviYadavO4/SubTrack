package com.portfolio.subtrack.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.subtrack.domain.model.BillingCycle
import com.portfolio.subtrack.domain.model.Category
import com.portfolio.subtrack.domain.model.Subscription
import com.portfolio.subtrack.presentation.theme.PrimaryAccent
import java.util.concurrent.TimeUnit

private data class QuickPreset(
    val name: String,
    val amount: String,
    val category: Category,
    val colorHex: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionSheet(
    existingSubscription: Subscription?,
    onDismiss: () -> Unit,
    onSave: (Subscription) -> Unit
) {
    var name by remember { mutableStateOf(existingSubscription?.name ?: "") }
    var amount by remember { mutableStateOf(existingSubscription?.amount?.toString() ?: "") }
    var selectedCycle by remember {
        mutableStateOf(existingSubscription?.billingCycle ?: BillingCycle.MONTHLY)
    }
    var selectedCategory by remember {
        mutableStateOf(existingSubscription?.category ?: Category.ENTERTAINMENT)
    }
    var daysUntilNext by remember {
        val initialDays = existingSubscription?.let {
            TimeUnit.MILLISECONDS.toDays(it.nextRenewalDate - System.currentTimeMillis())
                .coerceAtLeast(1)
                .toString()
        } ?: "30"
        mutableStateOf(initialDays)
    }

    val presetColors = listOf(
        0xFFE50914, // Netflix Red
        0xFF1DB954, // Spotify Green
        0xFF38BDF8, // Sky Blue
        0xFF6366F1, // Electric Indigo
        0xFFA855F7, // Purple
        0xFFF59E0B  // Amber
    )
    var selectedColor by remember {
        mutableLongStateOf(existingSubscription?.colorHex ?: presetColors[3])
    }

    val quickPresets = remember {
        listOf(
            QuickPreset("Netflix", "649", Category.ENTERTAINMENT, 0xFFE50914),
            QuickPreset("Spotify", "119", Category.ENTERTAINMENT, 0xFF1DB954),
            QuickPreset("YouTube", "149", Category.ENTERTAINMENT, 0xFFE50914),
            QuickPreset("Prime", "299", Category.ENTERTAINMENT, 0xFF38BDF8),
            QuickPreset("Gym", "1500", Category.FITNESS, 0xFFF59E0B),
            QuickPreset("iCloud+", "75", Category.UTILITY, 0xFF6366F1)
        )
    }

    val isFormValid = name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Row with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingSubscription == null) "New Subscription" else "Edit Subscription",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Quick Fill Suggestions (Only when adding new)
            if (existingSubscription == null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "QUICK FILL",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(quickPresets) { preset ->
                            SuggestionChip(
                                onClick = {
                                    name = preset.name
                                    amount = preset.amount
                                    selectedCategory = preset.category
                                    selectedColor = preset.colorHex
                                },
                                label = {
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                shape = RoundedCornerShape(4.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            )
                        }
                    }
                }
            }

            // Service Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Service Name") },
                placeholder = { Text("e.g., Netflix, Gym, Broadband") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Amount & Renewal Days Row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                OutlinedTextField(
                    value = daysUntilNext,
                    onValueChange = { daysUntilNext = it },
                    label = { Text("Renews In") },
                    suffix = { Text("days", style = MaterialTheme.typography.bodySmall) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // Billing Cycle Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BILLING CYCLE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BillingCycle.entries.forEach { cycle ->
                        val isSelected = selectedCycle == cycle
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCycle = cycle },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(4.dp),
                            label = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cycle.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
// Category Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Category.entries) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            shape = RoundedCornerShape(4.dp),
                            label = { Text(category.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
// Accent Color Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "BRAND COLOR",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presetColors.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
// Save Button
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    val parsedDays = daysUntilNext.toLongOrNull() ?: 30L
                    if (isFormValid) {
                        val nextDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(parsedDays)
                        onSave(
                            Subscription(
                                id = existingSubscription?.id ?: 0,
                                name = name.trim(),
                                amount = parsedAmount,
                                billingCycle = selectedCycle,
                                category = selectedCategory,
                                nextRenewalDate = nextDate,
                                colorHex = selectedColor
                            )
                        )
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (existingSubscription == null) "Save Subscription" else "Update Subscription",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}