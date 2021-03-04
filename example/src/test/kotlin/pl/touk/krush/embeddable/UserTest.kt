package pl.touk.krush.embeddable

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import pl.touk.krush.base.BaseDatabaseTest

class UserTest : BaseDatabaseTest(){

    @Test
    fun shouldHandleEmbeddedTypes() {
        transaction {
            SchemaUtils.create(UserTable)

            // given
            val contactAddress = Address(city = "Warsaw", street = "Aleja Bohaterów Września", houseNumber = 9)
            val invoiceAddress = Address(city = "Warsaw", street = "Aleje Jerozolimskie", houseNumber = 0)
            val user = UserTable.insert(User(contactAddress = contactAddress, invoiceAddress = invoiceAddress))

            // when
            val selectedUsers = UserTable
                    .select { (UserTable.contactAddressCity.lowerCase() eq "warsaw") and (UserTable.contactAddressHouseNumber greaterEq 9) }
                    .toUserList()

            // then
            assertThat(selectedUsers).containsOnly(user)
        }
    }

    @Test
    fun shouldHandleNullableEmbeddedType() {
        transaction {
            SchemaUtils.create(UserTable)

            // given
            val contactAddress = Address(city = "Warsaw", street = "Aleja Bohaterów Września", houseNumber = 9)
            val user = UserTable.insert(User(contactAddress = contactAddress, invoiceAddress = null))

            // when
            val selectedUsers = UserTable.selectAll().toUserList()

            // then
            assertThat(selectedUsers).containsOnly(user)
        }
    }
}
