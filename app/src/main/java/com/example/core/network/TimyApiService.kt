package com.example.core.network

import com.example.core.network.dto.LoginRequestDto
import com.example.core.network.dto.LoginResponseDto
import com.example.core.network.dto.MarkReadResponseDto
import com.example.core.network.dto.NotificationsResponseDto
import com.example.core.network.dto.RefreshTokenRequestDto
import com.example.core.network.dto.RefreshTokenResponseDto
import com.example.core.network.dto.TimetableResponseDto
import com.example.core.network.dto.VenueAvailabilityDto
import com.example.core.network.dto.VenuesResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TimyApiService {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequestDto
    ): Response<RefreshTokenResponseDto>

    @GET("api/v1/timetables/schedule")
    suspend fun getSchedule(
        @Query("cohortId") cohortId: String? = null
    ): Response<TimetableResponseDto>

    @GET("api/v1/notifications")
    suspend fun getNotifications(): Response<NotificationsResponseDto>

    @PATCH("api/v1/notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") id: String
    ): Response<MarkReadResponseDto>

    @GET("api/v1/venues")
    suspend fun getVenues(
        @Query("query") query: String? = null
    ): Response<VenuesResponseDto>

    @GET("api/v1/venues/{id}/availability")
    suspend fun getVenueAvailability(
        @Path("id") venueId: String
    ): Response<VenueAvailabilityDto>
}
