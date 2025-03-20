package com.geniusapk.shopping.presentation.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.geniusapk.shopping.domain.models.UserData
import com.geniusapk.shopping.domain.models.UserDataParent
import com.geniusapk.shopping.presentation.navigation.SubNavigation
import com.geniusapk.shopping.presentation.screens.utils.AnimatedLoading
import com.geniusapk.shopping.presentation.screens.utils.LogOutAlertDialog
import com.geniusapk.shopping.presentation.viewModels.ShoppingAppViewModel
import com.geniusapk.shopping.ui.theme.SweetPink
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenUi(
    viewModel: ShoppingAppViewModel = hiltViewModel(),
    firebaseAuth: FirebaseAuth,
    navController: NavController
) {

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        coroutineScope.launch(Dispatchers.IO) {

        viewModel.getUserById(firebaseAuth.currentUser!!.uid)}

    }
    val profileScreenState = viewModel.profileScreenState.collectAsStateWithLifecycle()
    val upDateScreenState = viewModel.upDateScreenState.collectAsStateWithLifecycle()
    val userProfileImageState = viewModel.userProfileImageState.collectAsStateWithLifecycle()


    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }

    val isEdting = remember { mutableStateOf(false) }
    val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val imageUrl = remember { mutableStateOf("") }


    val firstName =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.fastName ?: "") }
    val lastName =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.lastName ?: "") }
    val email =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.email ?: "") }
    val phoneNumber =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.phoneNumber ?: "") }
    val address =
        remember { mutableStateOf(profileScreenState.value.userData?.userData?.address ?: "") }

    LaunchedEffect(profileScreenState.value.userData) {
        coroutineScope.launch(Dispatchers.IO) {

        profileScreenState.value.userData?.userData?.let { userData ->
            firstName.value = userData.fastName ?: ""
            lastName.value = userData.lastName ?: ""
            email.value = userData.email ?: ""
            phoneNumber.value = userData.phoneNumber ?: ""
            address.value = userData.address ?: ""
            imageUrl.value = userData.profileImage ?: ""
        }
        }
    }


    val pickMedia =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                //  viewModel.upDateUserData(uri)
                //uploadImageToDatabase(uri = uri) {
                //  imageUri.value = uri}
                viewModel.upLoadUserProfileImage(uri)
                imageUri.value = uri
            }
        }


    if (upDateScreenState.value.userData != null) {
        Toast.makeText(context, upDateScreenState.value.userData, Toast.LENGTH_SHORT).show()
    } else if (upDateScreenState.value.errorMessage != null) {
        Toast.makeText(context, upDateScreenState.value.errorMessage, Toast.LENGTH_SHORT).show()
    } else if (upDateScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedLoading()
        }
    }




    if (userProfileImageState.value.userData != null) {
        imageUrl.value = userProfileImageState.value.userData.toString()
    } else if (userProfileImageState.value.errorMessage != null) {
        Toast.makeText(context, userProfileImageState.value.errorMessage, Toast.LENGTH_SHORT).show()
    } else if (userProfileImageState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedLoading()
        }
    }


    if (profileScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedLoading()
        }
    } else if (profileScreenState.value.errorMessage != null) {
        Text(text = profileScreenState.value.errorMessage!!)

    } else if (profileScreenState.value.userData != null) {

        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(
                        "Profile", fontWeight = FontWeight.Bold,
                    )
                })
            }

        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {

// is staring we don,t have user iamge so we will show default image and  when user click on edit button then also user will se default image and if user select image then we will show that image then it will show user image

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Start)
                ) {
                    SubcomposeAsyncImage(
                        model = if (isEdting.value) imageUri.value else imageUrl.value,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, color = SweetPink, CircleShape)
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
                            is AsyncImagePainter.State.Error -> Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )

                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                    if (isEdting.value) {
                        IconButton(
                            onClick = {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Change Picture",
                                tint = Color.White
                            )
                        }
                    }
                }



                Spacer(modifier = Modifier.size(16.dp))

                Row {
                    OutlinedTextField(
                        value = firstName.value,
                        modifier = Modifier.weight(1f),
                        readOnly = if (isEdting.value) false else true,

                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SweetPink,
                            focusedBorderColor = SweetPink
                        ),
                        shape = RoundedCornerShape(10.dp),

                        onValueChange = {
                            firstName.value = it

                        },
                        label = { Text("First Name") }
                    )
                    Spacer(modifier = Modifier.size(16.dp))


                    OutlinedTextField(
                        value = lastName.value,
                        modifier = Modifier.weight(1f),
                        readOnly = if (isEdting.value) false else true,

                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SweetPink,
                            focusedBorderColor = SweetPink
                        ),
                        onValueChange = {
                            lastName.value = it
                        },
                        shape = RoundedCornerShape(10.dp),

                        label = { Text("Last Name") }

                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    value = email.value,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = if (isEdting.value) false else true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = SweetPink,
                        focusedBorderColor = SweetPink
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onValueChange = {
                        email.value = it
                    },

                    label = { Text("Email") })

                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    value = phoneNumber.value,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = if (isEdting.value) false else true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = SweetPink,
                        focusedBorderColor = SweetPink
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onValueChange = {
                        phoneNumber.value = it
                    },
                    label = { Text("Phone Number") }

                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    value = address.value,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        address.value = it
                    },
                    readOnly = if (isEdting.value) false else true,

                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = SweetPink,
                        focusedBorderColor = SweetPink
                    ),

                    label = { Text("Address") }
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedButton(
                    onClick = {
                        showDialog.value = true


                    },

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(SweetPink)
                ) {
                    Text("Log Out")
                }

                if (showDialog.value) {
                    LogOutAlertDialog(
                        onDismiss = {
                            showDialog.value = false
                        },
                        onConfirm = {
                            firebaseAuth.signOut()
                            navController.navigate(SubNavigation.LoginSingUpScreen)
                        }
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))

                if (isEdting.value == false) {
                    OutlinedButton(
                        onClick = {
                            isEdting.value = !isEdting.value

                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),

                        ) {
                        Text("Edit Profile")
                    }
                } else {
                    OutlinedButton(
                        onClick = {


                            val updatedUserData = UserData(
                                fastName = firstName.value,
                                lastName = lastName.value,
                                email = email.value,
                                phoneNumber = phoneNumber.value,
                                address = address.value,
                                profileImage = imageUrl.value
                            )
                            val userDataParent = UserDataParent(
                                nodeId = profileScreenState.value.userData!!.nodeId,
                                userData = updatedUserData
                            )
                            viewModel.upDateUserData(userDataParent)
                            isEdting.value = !isEdting.value


                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),

                        ) {
                        Text("Save Profile")
                    }


                }


            }
        }


    }
}






