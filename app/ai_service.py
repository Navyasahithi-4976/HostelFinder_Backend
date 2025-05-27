import httpx
from typing import List, Optional
from . import schemas
import os

class AIService:
    def __init__(self):
        self.api_url = os.getenv("DEEPSEEK_API_URL", "https://api.deepseek.com/v1")
        self.api_key = os.getenv("DEEPSEEK_API_KEY")
        self.headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

    async def get_smart_recommendations(
        self,
        pincode: str,
        budget: Optional[float] = None,
        preferences: Optional[List[str]] = None
    ) -> dict:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.api_url}/recommend",
                headers=self.headers,
                json={
                    "pincode": pincode,
                    "budget": budget,
                    "preferences": preferences or []
                }
            )
            return response.json()

    async def get_similar_locations(self, pincode: str) -> List[str]:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.api_url}/similar-locations",
                headers=self.headers,
                json={"pincode": pincode}
            )
            return response.json()

    async def suggest_facilities(self, preferences: List[str]) -> List[str]:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.api_url}/suggest-facilities",
                headers=self.headers,
                json={"preferences": preferences}
            )
            return response.json()

    async def suggest_price(self, location: str, facilities: List[str]) -> float:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.api_url}/suggest-price",
                headers=self.headers,
                json={
                    "location": location,
                    "facilities": facilities
                }
            )
            return response.json()["suggested_price"]
