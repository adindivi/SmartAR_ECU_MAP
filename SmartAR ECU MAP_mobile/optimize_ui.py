import os

html_path = "app/src/main/assets/SmartAR_ECU_MAP_Mobile.html"
with open(html_path, "r", encoding="utf-8") as f:
    html = f.read()

# 1. 모달 너비를 고정값(w-80, w-96)에서 반응형 퍼센트(w-[90%])로 수정
html = html.replace('w-80 shadow-2xl modal-animate-enter', 'w-[90%] max-w-[320px] shadow-2xl modal-animate-enter')
html = html.replace('w-96 max-h-[85vh]', 'w-[92%] max-w-[400px] max-h-[85vh]')

# 2. 폼 라벨의 가독성 및 줄바꿈 최적화 (break-keep, leading-snug 추가 및 mb 증가)
html = html.replace('class="block text-slate-400 font-semibold mb-1"', 'class="block text-slate-400 font-semibold mb-1.5 break-keep leading-snug"')
html = html.replace('class="block text-slate-400 font-semibold mb-1.5"', 'class="block text-slate-400 font-semibold mb-2 break-keep leading-snug"')

# 3. 모달 헤더 및 기타 에러 문구 정렬 (text-center 등)
html = html.replace('<h3 class="text-sm font-bold text-white mb-4 tracking-wide flex items-center">', '<h3 class="text-base font-bold text-white mb-4 tracking-wide flex items-center justify-center break-keep">')
html = html.replace('text-[10px] mb-3 hidden break-keep', 'text-xs mb-3 hidden break-keep text-center leading-relaxed')

# 4. 서브타이틀 및 부가 설명 텍스트 크기 상향 (11px -> 12px)
html = html.replace('text-[11px] text-appleTextMuted leading-relaxed break-keep', 'text-xs text-appleTextMuted leading-relaxed break-keep')
html = html.replace('text-[11px] font-semibold', 'text-xs font-semibold')
html = html.replace('text-[10px] font-mono', 'text-[11px] font-mono')

with open(html_path, "w", encoding="utf-8") as f:
    f.write(html)
print("UI optimized")
