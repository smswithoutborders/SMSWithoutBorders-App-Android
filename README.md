# SMSWithoutBorders - Android

### Publishing Payload
```python
import struct
import base64

platform_letter = b'g'
encrypted_content=b'...'
device_id=b'...'

payload = struct.pack("<i", len(encrypted_content)) + pl + encrypted_content + device_id
incoming_payload = base64.b64encode(payload)

# unpacking in Python
payload = base64.b64decode(incoming_payload)
len_enc_content = struct.unpack("<i", payload[:4])[0]
platform_letter = chr(payload[4])
encrypted_content = payload[5 : 5 + len_enc_content]
device_id = payload[5 + len_enc_content :]

# getting header from published messages
encrypted_payload = base64.b64decode(encrypted_content)
len_header = struct.unpack("<i", encrypted_payload[0:4])[0]
header = encrypted_payload[4: 4 + len_header]
content_ciphertext = encrypted_payload[4 + len_header:]
```

### Platform specific publications (encrypted content)
```python3
""" Email (Gmail etc)
"""
# to:cc:bcc:subject:body

""" Messages (Telegram etc)
"""
# to:body

""" Text (X; Twitter etc)
"""
# body
```
