// const speakButton = document.getElementById("speak-btn");
// const stopButton = document.getElementById("stop-btn");
const textInput = document.getElementById("text-input");
// const displayTextElement = document.getElementById("display-text");

let currentSentenceIndex = 0;

function detectLanguage(text) {
    const koreanRegex = /[\u3131-\uD79D]/;
    const japaneseRegex = /[\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFF]/;
    const chineseRegex = /[\u4e00-\u9fa5]/;

    if (koreanRegex.test(text)) {
        return "ko-KR";
    } else if (japaneseRegex.test(text)) {
        return "ja-JP";
    } else if (chineseRegex.test(text)) {
        return "zh-CN";
    } else {
        return "en-US";
    }
}

function updateDisplayText(sentences, currentIndex) {
    let newText = "";

    sentences.forEach((sentence, index) => {
        if (index === currentIndex) {
            newText += `<mark>${sentence}</mark>`;
        } else {
            newText += sentence;
        }
    });

    // displayTextElement.innerHTML = newText;
}

function speakText(ttsInput = "", cancelProcess = true) {
    // const text = textInput.value;
    const text = ttsInput;
    if (!text) return;

    if (cancelProcess)
        stopText();

    const sentences = text.match(/[^.!?]+(?:[.!?]+(?:\s|$)|\s+$|$)/g) || [text];

    for (let i = currentSentenceIndex; i < sentences.length; i++) {
        const sentence = sentences[i];
        const language = detectLanguage(sentence);
        let utterance = new SpeechSynthesisUtterance(sentence);
        utterance.lang = language;
        utterance.onstart = function () {
            // updateDisplayText(sentences, i);
        };
        if (sentence.slice(-1) === "." || sentence.slice(-1) === "。") {
            utterance.onend = function () {
                setTimeout(() => {
                    speechSynthesis.resume();
                }, 500);
            };
        }

        if (i === sentences.length - 1) {
            utterance.onend = function () {
                currentSentenceIndex = 0;
            };
        } else {
            utterance.onend = function () {
                currentSentenceIndex = i + 1;
            };
        }

        speechSynthesis.speak(utterance);
    }
}

function stopText() {
    speechSynthesis.cancel();
}

// speakButton.addEventListener("click", speakText);
// stopButton.addEventListener("click", stopText);
