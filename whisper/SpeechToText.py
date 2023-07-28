import openai
import apikey

openai.api_key = apikey.api_key

audio_file= open("input/RenaiCirculation.mp3", "rb")
transcript = openai.Audio.transcribe("whisper-1", audio_file)

print(transcript["text"])   

