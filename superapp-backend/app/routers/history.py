from fastapi import APIRouter, Depends
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app.deps import get_current_user
from app import models

router = APIRouter(prefix="/history", tags=["history"])

class HistoryOut(BaseModel):
    id: str
    strategy: str
    created_at: str
    summary: dict | None = None
    results_count: int


@router.get("", response_model=list[HistoryOut])
def get_history(db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    quotes = (
        db.query(models.Quote)
        .filter(models.Quote.user_id == user.id)
        .order_by(models.Quote.created_at.desc())
        .limit(50)
        .all()
    )
    result = []
    for q in quotes:
        best = min(q.results, key=lambda r: r.total, default=None)
        summary = None
        if best:
            chain = db.get(models.Chain, best.chain_id)
            summary = {
                "best_chain": chain.name if chain else "",
                "best_total": best.total,
                "items_found": best.items_found,
            }
        result.append(HistoryOut(
            id=q.id,
            strategy=q.strategy,
            created_at=q.created_at.isoformat(),
            summary=summary,
            results_count=len(q.results),
        ))
    return result
