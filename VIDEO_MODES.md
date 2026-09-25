# Video generation modes

The app supports two modes:

- `OFFLINE_CINEMATIC`: no API key; creates a real H.264 MP4 from local rendered frames and saves it under `Movies/Qismat AI`.
- `CLOUD_AI`: optional Magic Hour generation; only appears when `MAGIC_HOUR_API_KEY` is configured in local `.env`.

The cloud key must never be committed to GitHub. The offline mode is the default and does not require internet access.
