package com.superapp.data.api

import com.superapp.data.api.dto.*
import retrofit2.http.*

interface SuperApi {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterDto): TokenPairDto

    @POST("auth/login")
    suspend fun login(@Body req: LoginDto): TokenPairDto

    @GET("users/me")
    suspend fun me(): UserDto

    @GET("chains")
    suspend fun chains(): List<ChainDto>

    @GET("stores/nearby")
    suspend fun nearby(@Query("lat") lat: Double, @Query("lng") lng: Double, @Query("limit") limit: Int = 20): List<StoreDto>

    @GET("shopping-lists")
    suspend fun myLists(): List<ShoppingListDto>

    @POST("shopping-lists")
    suspend fun createList(@Body req: CreateListDto): ShoppingListDto

    @GET("shopping-lists/{id}")
    suspend fun getList(@Path("id") id: String): ShoppingListDto

    @POST("shopping-lists/{id}/items")
    suspend fun addItem(@Path("id") id: String, @Body req: ListItemDto): ListItemDto

    @DELETE("shopping-lists/{id}/items/{itemId}")
    suspend fun deleteItem(@Path("id") id: String, @Path("itemId") itemId: String)

    @GET("search")
    suspend fun search(@Query("q") q: String, @Query("limit") limit: Int = 25): SearchResponseDto

    @POST("quote")
    suspend fun quote(@Body req: QuoteRequestDto): QuoteResponseDto

    @GET("quote/{id}")
    suspend fun getQuote(@Path("id") id: String): QuoteResponseDto

    @GET("favorites")
    suspend fun favorites(): List<FavoriteDto>

    @POST("favorites")
    suspend fun addFavorite(@Body req: FavoriteRequestDto): FavoriteDto

    @DELETE("favorites/{productId}")
    suspend fun removeFavorite(@Path("productId") productId: String)

    @GET("alerts")
    suspend fun alerts(): List<AlertDto>

    @POST("alerts")
    suspend fun addAlert(@Body req: AlertRequestDto): AlertDto

    @GET("history")
    suspend fun history(): List<HistoryDto>

    @POST("scraping/run")
    suspend fun runScraping(@Body req: RunScrapingDto = RunScrapingDto()): Map<String, String>

    @GET("scraping/jobs")
    suspend fun scrapingJobs(): List<ScrapingJobDto>
}

