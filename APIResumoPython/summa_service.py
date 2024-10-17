from flask import Flask, request, jsonify
from summa import summarizer
from pdfminer.high_level import extract_text
import os

app = Flask(__name__)

@app.route('/summarize', methods=['POST'])
def summarize():
    try:
        if 'file' not in request.files:
            return jsonify({'error': 'No file provided'}), 400
        
        file = request.files['file']

        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400

        if not file.filename.endswith('.pdf'):
            return jsonify({'error': 'File is not a PDF'}), 400

        temp_file_path = 'temp.pdf'
        file.save(temp_file_path)

        text = extract_text(temp_file_path)

        ratio = request.form.get('ratio', 0.2) 
        ratio = float(ratio) 

        if not text:
            return jsonify({'error': 'No text extracted from PDF'}), 400

        summary = summarizer.summarize(text, ratio=ratio)

        os.remove(temp_file_path)

        return jsonify({'summary': summary}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
