from __future__ import annotations

from datetime import datetime, timezone
from typing import Literal
import uuid

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr, Field, field_validator


class LeadPayload(BaseModel):
    name: str = Field(min_length=1, max_length=140)
    email: EmailStr = Field(max_length=320)
    company: str | None = Field(default=None, max_length=140)
    product: Literal[
        "aurum-core",
        "aurum-orchestrator",
        "aurum-sovereign"
    ] | None = None
    intent: str | None = Field(default=None, max_length=2000)
    ts: str | None = None

    @field_validator("name", "company", "intent")
    @classmethod
    def clean_text(cls, value: str | None) -> str | None:
        if value is None:
            return value

        cleaned = " ".join(value.split())

        if not cleaned:
            return None

        return cleaned


class LeadRecord(LeadPayload):
    id: str
    received_at: datetime


app = FastAPI(title="Aurum Conversion API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET", "POST", "OPTIONS"],
    allow_headers=["*"],
)

LEADS: dict[str, LeadRecord] = {}


@app.get("/healthz")
def healthz() -> dict[str, str]:
    return {"status": "operational"}


@app.post("/api/leads", response_model=LeadRecord, status_code=201)
def create_lead(payload: LeadPayload) -> LeadRecord:
    lead_id = uuid.uuid4().hex
    record = LeadRecord(
        **payload.model_dump(),
        id=lead_id,
        received_at=datetime.now(timezone.utc),
    )
    LEADS[lead_id] = record
    return record


@app.get("/api/leads/{lead_id}", response_model=LeadRecord)
def read_lead(lead_id: str) -> LeadRecord:
    record = LEADS.get(lead_id)

    if not record:
        raise HTTPException(status_code=404, detail="Lead not found")

    return record
