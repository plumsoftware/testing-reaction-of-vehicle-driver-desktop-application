package allusers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.regular.user.User
import domain.storage.UserStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.components.SearchField
import other.components.UserButton
import other.extension.padding.ExtensionPadding
import other.extension.route.DesktopRouting
import allusers.store.Effect
import allusers.store.Event
import allusers.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersPage(navigator: Navigator, userStorage: UserStorage, block: (User) -> Unit = {}) {

    val usersViewModel: UsersViewModel =
        viewModel(modelClass = UsersViewModel::class) {
            UsersViewModel(
                userStorage = userStorage
            )
        }

    val state = usersViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        usersViewModel.onEvent(Event.InitUsers)
        usersViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackCLicked -> {
                    navigator.goBack()
                }

                is Effect.UserCLicked -> {
                    block.invoke(effect.selectedUser)
                    navigator.navigate(route = DesktopRouting.aboutuser)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = ExtensionPadding.mediumAsymmetricalContentPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                BackButton(
                    onClick = { usersViewModel.onEvent(Event.BackClicked) }
                )
                SearchField(onSearchClick = {
                    usersViewModel.onEvent(Event.OnSearch(query = it))
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = ExtensionPadding.scaffoldTopPadding)
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.largeVerticalArrangementTop,
            horizontalAlignment = Alignment.Start
        ) {
            itemsIndexed(state.value.searchList) { _, item ->
                UserButton(
                    onClick = {
                        usersViewModel.onEvent(Event.OnUserClick(user = item))
                    },
                    text = "${item.surname} ${item.name} ${item.patronymic}"
                )
            }
        }
    }
}