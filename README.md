# SMSWithoutBorders - Android

### Publishing Payload
```python3
platform_letter = b'g'
encrypted_content=b'...'
device_id=b'...'

payload = len(encrypted_content) + pl + encrypted_content + device_id
return base64.encode(payload)

# unpacking in Python
import struct
import base64

payload = base64.decode(incoming_payload)
len_enc_content = struct.unpack("<i", payload[0])
platform_letter = payload[1]
encrypted_content = payload[2:len_enc_content]
device_id = payload[2+len_enc_content:]

# getting header from published messages
len_header = struct.unpack("<i", encrypted_content[0:4])
header = encrypted_content[4: 4 + len_header]
encrypted_content = encrypted_contend[4 + len_header:]
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
