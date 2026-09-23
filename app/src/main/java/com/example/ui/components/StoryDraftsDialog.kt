package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StoryDraftEntity
import com.example.ui.theme.CinematicBg
import com.example.ui.theme.CinematicBorder
import com.example.ui.theme.CinematicSurface
import com.example.ui.theme.CinematicSurfaceElevated
import com.example.ui.theme.CinematicTextDim
import com.example.ui.theme.CinematicTextMuted
import com.example.ui.theme.CinematicWhite
import com.example.ui.theme.QismatEmerald
import com.example.ui.theme.QismatGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StoryDraftsDialog(
  drafts: List<StoryDraftEntity>,
  onDismiss: () -> Unit,
  onSelectDraft: (StoryDraftEntity) -> Unit,
  onDeleteDraft: (Long) -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CinematicSurfaceElevated,
    shape = RoundedCornerShape(20.dp),
    modifier = Modifier.testTag("story_drafts_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(QismatGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = "Drafts",
              tint = QismatGold,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Saved Story Drafts",
              color = CinematicWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "Local Room Database (${drafts.size} saved)",
              color = QismatGold,
              fontSize = 11.sp
            )
          }
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = CinematicTextMuted
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 380.dp)
      ) {
        if (drafts.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "📝", fontSize = 32.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Koi saved draft nahi hai",
                color = CinematicWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Studio mein kahani likh kar 'Draft Save Karein' dabayein.",
                color = CinematicTextMuted,
                fontSize = 11.sp
              )
            }
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(drafts, key = { it.id }) { draft ->
              val dateStr = try {
                val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                sdf.format(Date(draft.updatedAt))
              } catch (e: Exception) {
                ""
              }

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, CinematicBorder, RoundedCornerShape(12.dp))
                  .clickable { onSelectDraft(draft) }
                  .testTag("draft_item_${draft.id}"),
                colors = CardDefaults.cardColors(containerColor = CinematicSurface)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = draft.title,
                      color = CinematicWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.weight(1f)
                    )

                    IconButton(
                      onClick = { onDeleteDraft(draft.id) },
                      modifier = Modifier.size(24.dp).testTag("delete_draft_${draft.id}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Draft",
                        tint = CinematicTextDim,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }

                  val previewText = when {
                    draft.duaText.isNotBlank() -> draft.duaText
                    draft.storyPrompt.isNotBlank() -> draft.storyPrompt
                    else -> "No prompt written yet."
                  }

                  Text(
                    text = previewText,
                    color = CinematicTextMuted,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "📍 ${draft.location} • $dateStr",
                      color = QismatEmerald,
                      fontSize = 10.sp
                    )

                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.clickable { onSelectDraft(draft) }
                    ) {
                      Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore",
                        tint = QismatGold,
                        modifier = Modifier.size(14.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Load",
                        color = QismatGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = QismatGold, contentColor = Color.Black),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Done", fontWeight = FontWeight.Bold)
      }
    }
  )
}
