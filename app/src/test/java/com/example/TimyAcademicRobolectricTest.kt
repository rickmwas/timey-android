package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.model.DayOfWeekEnum
import com.example.core.model.NotificationType
import com.example.core.model.SessionStatus
import com.example.core.model.TimetableSession
import com.example.core.model.TimyNotification
import com.example.core.model.Venue
import com.example.core.model.VenueStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TimyAcademicRobolectricTest {

    @Test
    fun `test app name matches Timy in resources`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Timy", appName)
    }

    @Test
    fun `test university name and tagline resources`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val uniName = context.getString(R.string.university_name)
        val tagline = context.getString(R.string.app_tagline)
        assertEquals("Kisii University", uniName)
        assertEquals("Academic Timetable. Zero Ambiguity.", tagline)
    }

    @Test
    fun `test timetable session status and venue relocation`() {
        val rescheduledSession = TimetableSession(
            id = "sess_04",
            unitCode = "SOEN 313",
            unitTitle = "Cloud Computing & Distributed Systems",
            lecturer = "Dr. Beatrice Bosire",
            venue = "NSC Lab 2",
            originalVenue = "LH 14",
            startTime = "14:00",
            endTime = "17:00",
            dayOfWeek = DayOfWeekEnum.TUESDAY,
            status = SessionStatus.RESCHEDULED,
            cancellationReason = "Upgraded to High-Performance Cloud Computing Lab"
        )

        assertEquals(SessionStatus.RESCHEDULED, rescheduledSession.status)
        assertEquals("LH 14", rescheduledSession.originalVenue)
        assertEquals("NSC Lab 2", rescheduledSession.venue)
        assertNotNull(rescheduledSession.cancellationReason)
    }

    @Test
    fun `test timetable cancellation session has reason`() {
        val cancelledSession = TimetableSession(
            id = "sess_07",
            unitCode = "COMP 318",
            unitTitle = "Research Methodology in Computing",
            lecturer = "Prof. George Ondieki",
            venue = "LH 26",
            startTime = "14:00",
            endTime = "17:00",
            dayOfWeek = DayOfWeekEnum.THURSDAY,
            status = SessionStatus.CANCELLED,
            cancellationReason = "Lecturer attending Faculty Board Defense"
        )

        assertEquals(SessionStatus.CANCELLED, cancelledSession.status)
        assertEquals("COMP 318", cancelledSession.unitCode)
        assertTrue(cancelledSession.cancellationReason!!.contains("Faculty Board Defense"))
    }

    @Test
    fun `test day of week mapping`() {
        assertEquals(DayOfWeekEnum.MONDAY, DayOfWeekEnum.fromDayNumber(1))
        assertEquals(DayOfWeekEnum.TUESDAY, DayOfWeekEnum.fromDayNumber(2))
        assertEquals(DayOfWeekEnum.SATURDAY, DayOfWeekEnum.fromDayNumber(6))

        assertEquals(DayOfWeekEnum.MONDAY, DayOfWeekEnum.fromShortName("Mon"))
        assertEquals(DayOfWeekEnum.FRIDAY, DayOfWeekEnum.fromShortName("Fri"))
    }

    @Test
    fun `test venue availability state derivation`() {
        val availableVenue = Venue(
            id = "v_nsc1",
            code = "NSC Lab 1",
            name = "New Science Complex Lab 1",
            building = "Computer Science Wing",
            capacity = 60,
            status = VenueStatus.AVAILABLE,
            availableUntil = "11:00"
        )

        val occupiedVenue = Venue(
            id = "v_lh26",
            code = "LH 26",
            name = "Lecture Hall 26",
            building = "Main Academic Pavilion",
            capacity = 180,
            status = VenueStatus.OCCUPIED,
            currentUnitCode = "SOEN 311",
            availableUntil = "11:00"
        )

        assertEquals(VenueStatus.AVAILABLE, availableVenue.status)
        assertEquals("11:00", availableVenue.availableUntil)

        assertEquals(VenueStatus.OCCUPIED, occupiedVenue.status)
        assertEquals("SOEN 311", occupiedVenue.currentUnitCode)
    }

    @Test
    fun `test notification filtering and read state`() {
        val notif1 = TimyNotification(
            id = "n1",
            title = "Venue Moved",
            message = "SOEN 313 moved to NSC Lab 2",
            type = NotificationType.VENUE_CHANGE,
            unitCode = "SOEN 313",
            isRead = false
        )

        val notif2 = TimyNotification(
            id = "n2",
            title = "Cancelled",
            message = "COMP 318 cancelled",
            type = NotificationType.CANCELLATION,
            unitCode = "COMP 318",
            isRead = true
        )

        assertFalse(notif1.isRead)
        assertTrue(notif2.isRead)
        assertEquals(NotificationType.VENUE_CHANGE, notif1.type)
        assertEquals(NotificationType.CANCELLATION, notif2.type)
    }

    @Test
    fun `test app theme modes for X mobile design`() {
        val lightsOut = com.example.ui.theme.AppThemeMode.LIGHTS_OUT
        val dim = com.example.ui.theme.AppThemeMode.DIM
        val light = com.example.ui.theme.AppThemeMode.LIGHT

        assertEquals("Lights Out", lightsOut.displayName)
        assertEquals("Dim", dim.displayName)
        assertEquals("Pure Light", light.displayName)
        assertTrue(lightsOut.description.contains("#000000"))
        assertTrue(dim.description.contains("#15202B"))
    }
}
