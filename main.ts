import express, { type NextFunction, type Request, type Response } from "express";
import crypto from "node:crypto";
import { z } from "zod";

const app = express();

app.use(express.json({ limit: "1mb" }));

app.use((req: Request, res: Response, next: NextFunction) => {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type");

  if (req.method === "OPTIONS") {
    res.sendStatus(204);
    return;
  }

  next();
});

const leadSchema = z.object({
  name: z.string().trim().min(1).max(140),
  email: z.string().trim().email().max(320),
  company: z.string().trim().max(140).optional().nullable(),
  product: z
    .enum(["aurum-core", "aurum-orchestrator", "aurum-sovereign"])
    .optional()
    .nullable(),
  intent: z.string().trim().max(2000).optional().nullable(),
  ts: z.string().datetime().optional().nullable()
});

type LeadPayload = z.infer<typeof leadSchema>;

interface LeadRecord extends LeadPayload {
  id: string;
  receivedAt: string;
}

const leads = new Map<string, LeadRecord>();

app.get("/healthz", (_req: Request, res: Response) => {
  res.json({ status: "operational" });
});

app.post("/api/leads", (req: Request, res: Response) => {
  const parsed = leadSchema.safeParse(req.body);

  if (!parsed.success) {
    res.status(422).json({
      error: "Invalid payload",
      issues: parsed.error.issues
    });
    return;
  }

  const id = crypto.randomUUID();

  const record: LeadRecord = {
    ...parsed.data,
    id,
    receivedAt: new Date().toISOString()
  };

  leads.set(id, record);

  res.status(201).json(record);
});

app.get("/api/leads/:id", (req: Request, res: Response) => {
  const record = leads.get(req.params.id);

  if (!record) {
    res.status(404).json({ error: "Lead not found" });
    return;
  }

  res.json(record);
});

app.use((_req: Request, res: Response) => {
  res.status(404).json({ error: "Not found" });
});

app.use((error: Error, _req: Request, res: Response, _next: NextFunction) => {
  if (error instanceof SyntaxError) {
    res.status(400).json({ error: "Invalid JSON" });
    return;
  }

  console.error(error);
  res.status(500).json({ error: "Internal server error" });
});

const port = Number(process.env.PORT ?? 8787);

app.listen(port, () => {
  console.log(`TypeScript API listening on ${port}`);
});
